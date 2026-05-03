package com.qbank.group.application;

import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
import com.qbank.group.application.dto.AddQuestionRequest;
import com.qbank.group.application.dto.GroupRequest;
import com.qbank.group.application.dto.GroupResponse;
import com.qbank.group.domain.GroupItemCountProjection;
import com.qbank.group.domain.QuestionGroup;
import com.qbank.group.domain.QuestionGroupItem;
import com.qbank.group.domain.QuestionGroupItemRepository;
import com.qbank.group.domain.QuestionGroupRepository;
import com.qbank.question.application.QuestionService;
import com.qbank.question.application.dto.StudyQuestionSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final QuestionGroupRepository groupRepository;
    private final QuestionGroupItemRepository groupItemRepository;
    private final QuestionService questionService;

    public List<GroupResponse> getMyGroups(Long userId) {
        List<QuestionGroup> groups = groupRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return attachItemCounts(groups);
    }

    @Transactional
    public GroupResponse createGroup(GroupRequest request, Long userId) {
        QuestionGroup group = QuestionGroup.create(userId, request.name(), request.description(), request.isPublic());
        QuestionGroup saved = groupRepository.save(group);
        return GroupResponse.of(saved, 0L);
    }

    @Transactional
    public GroupResponse updateGroup(Long groupId, GroupRequest request, Long userId) {
        QuestionGroup group = findOwnedGroup(groupId, userId);
        group.update(request.name(), request.description(), request.isPublic());
        long count = groupItemRepository.countByGroupId(groupId);
        return GroupResponse.of(group, count);
    }

    @Transactional
    public void deleteGroup(Long groupId, Long userId) {
        findOwnedGroup(groupId, userId);
        groupItemRepository.deleteAllByGroupId(groupId);
        groupRepository.deleteById(groupId);
    }

    public GroupResponse getGroup(Long groupId, Long userId) {
        QuestionGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));
        if (!group.isPublic() && !Objects.equals(group.getUserId(), userId)) {
            throw new BusinessException(ErrorCode.GROUP_ACCESS_DENIED);
        }
        long count = groupItemRepository.countByGroupId(groupId);
        return GroupResponse.of(group, count);
    }

    public List<GroupResponse> getPublicGroupsByUser(Long targetUserId) {
        List<QuestionGroup> groups = groupRepository.findByUserIdAndIsPublicTrueOrderByCreatedAtDesc(targetUserId);
        return attachItemCounts(groups);
    }

    private List<GroupResponse> attachItemCounts(List<QuestionGroup> groups) {
        if (groups.isEmpty()) return List.of();
        List<Long> groupIds = groups.stream().map(QuestionGroup::getId).toList();
        Map<Long, Long> countMap = groupItemRepository.countByGroupIdIn(groupIds)
                .stream().collect(Collectors.toMap(GroupItemCountProjection::getGroupId, GroupItemCountProjection::getCount));
        return groups.stream()
                .map(g -> GroupResponse.of(g, countMap.getOrDefault(g.getId(), 0L)))
                .toList();
    }

    public List<StudyQuestionSummary> getGroupStudyQuestions(Long groupId, Long userId) {
        QuestionGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));
        if (!group.isPublic() && !Objects.equals(group.getUserId(), userId)) {
            throw new BusinessException(ErrorCode.GROUP_ACCESS_DENIED);
        }
        List<Long> questionIds = groupItemRepository.findByGroupId(groupId)
                .stream().map(QuestionGroupItem::getQuestionId).toList();
        return questionService.getStudyQuestionsByIds(questionIds, userId);
    }

    @Transactional
    public void addQuestion(Long groupId, AddQuestionRequest request, Long userId) {
        findOwnedGroup(groupId, userId);
        if (!groupItemRepository.existsByGroupIdAndQuestionId(groupId, request.questionId())) {
            groupItemRepository.save(QuestionGroupItem.of(groupId, request.questionId()));
        }
    }

    @Transactional
    public void removeQuestion(Long groupId, Long questionId, Long userId) {
        findOwnedGroup(groupId, userId);
        groupItemRepository.deleteByGroupIdAndQuestionId(groupId, questionId);
    }

    public List<Long> getGroupIdsContaining(Long questionId, Long userId) {
        List<Long> allGroupIds = groupItemRepository.findGroupIdsByQuestionId(questionId);
        if (allGroupIds.isEmpty()) return List.of();
        return groupRepository.findAllById(allGroupIds)
                .stream()
                .filter(g -> Objects.equals(g.getUserId(), userId))
                .map(QuestionGroup::getId)
                .toList();
    }

    private QuestionGroup findOwnedGroup(Long groupId, Long userId) {
        QuestionGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));
        if (!Objects.equals(group.getUserId(), userId)) {
            throw new BusinessException(ErrorCode.GROUP_ACCESS_DENIED);
        }
        return group;
    }
}
