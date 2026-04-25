package com.qbank.question.application;

import com.qbank.bookmark.domain.BookmarkCountProjection;
import com.qbank.bookmark.domain.BookmarkRepository;
import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
import com.qbank.question.application.dto.QuestionDetail;
import com.qbank.question.application.dto.QuestionSearchCondition;
import com.qbank.question.application.dto.QuestionStatsResponse;
import com.qbank.question.application.dto.QuestionSummary;
import com.qbank.question.application.dto.RegisterQuestion;
import com.qbank.question.domain.*;
import com.qbank.tag.domain.Tag;
import com.qbank.tag.domain.TagRepository;
import com.qbank.user.domain.User;
import com.qbank.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionTagRepository questionTagRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final BookmarkRepository bookmarkRepository;

    public Page<QuestionSummary.Response> getPublicQuestions(QuestionSummary.Request dto) {
        Specification<Question> spec = QuestionSpecification.publicSearch(QuestionSearchCondition.of(dto));
        Page<Question> questions = questionRepository.findAll(spec, dto.pageable());
        if (questions.isEmpty()) return Page.empty(dto.pageable());
        return enrich(questions, dto.userId());
    }

    public Page<QuestionSummary.Response> getMyQuestions(Visibility visibility, Pageable pageable, Long userId) {
        Specification<Question> spec = QuestionSpecification.myQuestions(userId, visibility);
        Page<Question> questions = questionRepository.findAll(spec, pageable);
        if (questions.isEmpty()) return Page.empty(pageable);
        return enrich(questions, userId);
    }

    public Page<QuestionSummary.Response> getBookmarkedQuestions(Pageable pageable, Long userId) {
        Specification<Question> spec = QuestionSpecification.isBookmarkedByUser(userId);
        Page<Question> questions = questionRepository.findAll(spec, pageable);
        if (questions.isEmpty()) return Page.empty(pageable);
        return enrich(questions, userId);
    }

    private Page<QuestionSummary.Response> enrich(Page<Question> questions, Long userId) {
        List<Long> questionIds = questions.getContent().stream().map(Question::getId).toList();
        Set<Long> authorIds = questions.getContent().stream().map(Question::getAuthorId).collect(Collectors.toSet());

        Map<Long, List<QuestionSummary.Response.TagInfo>> tagsByQuestionId = buildTagMap(questionIds);
        Map<Long, String> nicknameByAuthorId = buildNicknameMap(authorIds);
        Map<Long, Long> bookmarkCountByQuestionId = buildBookmarkCountMap(questionIds);
        Set<Long> bookmarkedIds = userId != null
                ? new HashSet<>(bookmarkRepository.findBookmarkedQuestionIds(userId, questionIds))
                : Set.of();

        return QuestionSummary.Response.of(questions, tagsByQuestionId, nicknameByAuthorId,
                bookmarkCountByQuestionId, bookmarkedIds, userId);
    }

    private Map<Long, List<QuestionSummary.Response.TagInfo>> buildTagMap(List<Long> questionIds) {
        return questionTagRepository.findAllWithTagByQuestionIdIn(questionIds)
                .stream()
                .collect(Collectors.groupingBy(
                        qt -> qt.getQuestion().getId(),
                        Collectors.mapping(
                                qt -> new QuestionSummary.Response.TagInfo(qt.getTag().getId(), qt.getTag().getName()),
                                Collectors.toList()
                        )
                ));
    }

    private Map<Long, String> buildNicknameMap(Set<Long> authorIds) {
        return userRepository.findAllById(authorIds)
                .stream()
                .collect(Collectors.toMap(User::getId, User::getNickname));
    }

    private Map<Long, Long> buildBookmarkCountMap(List<Long> questionIds) {
        return bookmarkRepository.countByQuestionIdIn(questionIds)
                .stream()
                .collect(Collectors.toMap(BookmarkCountProjection::getQuestionId, BookmarkCountProjection::getCount));
    }

    public QuestionDetail.Response getDetail(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        if (question.getVisibility() == Visibility.PRIVATE
                && !Objects.equals(question.getAuthorId(), userId)) {
            throw new BusinessException(ErrorCode.QUESTION_NOT_FOUND);
        }

        boolean isMyQuestion = Objects.equals(question.getAuthorId(), userId);

        List<QuestionDetail.Response.TagInfo> tags = questionTagRepository
                .findAllWithTagByQuestionIdIn(List.of(questionId))
                .stream()
                .map(qt -> new QuestionDetail.Response.TagInfo(qt.getTag().getId(), qt.getTag().getName()))
                .toList();

        String authorNickname = userRepository.findById(question.getAuthorId())
                .map(User::getNickname)
                .orElse("알 수 없음");

        boolean isBookmarked = userId != null
                && bookmarkRepository.existsByUserIdAndQuestionId(userId, questionId);
        long bookmarkCount = bookmarkRepository.countByQuestionId(questionId);

        return new QuestionDetail.Response(
                question.getId(),
                question.getTitle(),
                tags,
                question.getCareerLevel(),
                question.getDifficulty(),
                question.getVisibility(),
                authorNickname,
                isMyQuestion,
                isBookmarked,
                bookmarkCount,
                isMyQuestion ? question.getMyNotes() : null,
                isMyQuestion ? question.getKeyPoints() : List.of(),
                isMyQuestion ? question.getMemo() : null,
                question.getCreatedAt()
        );
    }

    @Transactional
    public RegisterQuestion.Response register(RegisterQuestion.Request request, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        List<Tag> tags = resolveTags(request.tagIds());

        Question question = Question.create(
                userId,
                request.title(),
                request.getCareerLevel(),
                request.getVisibility(),
                request.myNotes(),
                request.keyPoints(),
                request.memo()
        );
        Question saved = questionRepository.save(question);

        List<QuestionTag> questionTags = tags.stream()
                .map(tag -> QuestionTag.of(saved, tag))
                .toList();
        questionTagRepository.saveAll(questionTags);

        return new RegisterQuestion.Response(saved.getId());
    }

    @Transactional
    public void update(Long questionId, RegisterQuestion.Request request, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        if (!Objects.equals(question.getAuthorId(), userId)) {
            throw new BusinessException(ErrorCode.QUESTION_ACCESS_DENIED);
        }

        List<Tag> tags = resolveTags(request.tagIds());

        question.update(
                request.title(),
                request.getCareerLevel(),
                request.getVisibility(),
                request.myNotes(),
                request.keyPoints(),
                request.memo()
        );

        questionTagRepository.deleteAllByQuestionId(questionId);
        List<QuestionTag> newTags = tags.stream()
                .map(tag -> QuestionTag.of(question, tag))
                .toList();
        questionTagRepository.saveAll(newTags);
    }

    @Transactional
    public void delete(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        if (!Objects.equals(question.getAuthorId(), userId)) {
            throw new BusinessException(ErrorCode.QUESTION_ACCESS_DENIED);
        }

        questionTagRepository.deleteAllByQuestionId(questionId);
        questionRepository.delete(question);
    }

    private List<Tag> resolveTags(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return List.of();
        List<Tag> found = tagRepository.findAllById(tagIds);
        if (found.size() != tagIds.size()) {
            throw new BusinessException(ErrorCode.TAG_NOT_FOUND);
        }
        return found;
    }

    public QuestionStatsResponse getStats(Long userId) {
        long totalQuestions = questionRepository.countByVisibility(Visibility.PUBLIC);
        long myQuestions = userId != null ? questionRepository.countByAuthorId(userId) : 0L;
        long myBookmarks = userId != null ? bookmarkRepository.countByUserId(userId) : 0L;
        return new QuestionStatsResponse(totalQuestions, myQuestions, myBookmarks);
    }
}
