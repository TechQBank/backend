package com.qbank.question.application;

import com.qbank.bookmark.domain.BookmarkCountProjection;
import com.qbank.bookmark.domain.BookmarkRepository;
import com.qbank.question.application.dto.QuestionSearchCondition;
import com.qbank.question.application.dto.QuestionStatsResponse;
import com.qbank.question.application.dto.QuestionSummary;
import com.qbank.question.domain.Question;
import com.qbank.question.domain.QuestionRepository;
import com.qbank.question.domain.QuestionTagRepository;
import com.qbank.question.domain.Visibility;
import com.qbank.user.domain.User;
import com.qbank.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionTagRepository questionTagRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;

    public Page<QuestionSummary.Response> getPublicQuestions(QuestionSummary.Request dto) {

        Specification<Question> spec = QuestionSpecification.publicSearch(QuestionSearchCondition.of(dto));

        Page<Question> questions = questionRepository.findAll(spec, dto.pageable());

        if (questions.isEmpty()) {
            return Page.empty(dto.pageable());
        }

        List<Long> questionIds = questions.getContent().stream()
                .map(Question::getId)
                .toList();

        Set<Long> authorIds = questions.getContent().stream()
                .map(Question::getAuthorId)
                .collect(Collectors.toSet());

        // 태그 배치 조회
        Map<Long, List<QuestionSummary.Response.TagInfo>> tagsByQuestionId =
                questionTagRepository.findAllWithTagByQuestionIdIn(questionIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                qt -> qt.getQuestion().getId(),
                                Collectors.mapping(
                                        qt -> new QuestionSummary.Response.TagInfo(qt.getTag().getId(), qt.getTag().getName()),
                                        Collectors.toList()
                                )
                        ));

        // 작성자 닉네임 배치 조회
        Map<Long, String> nicknameByAuthorId = userRepository.findAllById(authorIds)
                .stream()
                .collect(Collectors.toMap(User::getId, User::getNickname));

        // 북마크 수 배치 조회
        Map<Long, Long> bookmarkCountByQuestionId = bookmarkRepository
                .countByQuestionIdIn(questionIds)
                .stream()
                .collect(Collectors.toMap(
                        BookmarkCountProjection::getQuestionId,
                        BookmarkCountProjection::getCount
                ));

        // 내가 북마크한 질문 ID 조회
        Set<Long> bookmarkedQuestionIds = dto.userId() != null
                ? new HashSet<>(bookmarkRepository.findBookmarkedQuestionIds(dto.userId(), questionIds))
                : Set.of();

        return QuestionSummary.Response.of(
                questions,
                tagsByQuestionId,
                nicknameByAuthorId,
                bookmarkCountByQuestionId,
                bookmarkedQuestionIds,
                dto.userId()
        );
    }

    public QuestionStatsResponse getStats(Long userId) {
        long totalQuestions = questionRepository.countByVisibility(Visibility.PUBLIC);
        long myQuestions = userId != null ? questionRepository.countByAuthorId(userId) : 0L;
        long myBookmarks = userId != null ? bookmarkRepository.countByUserId(userId) : 0L;
        return new QuestionStatsResponse(totalQuestions, myQuestions, myBookmarks);
    }
}
