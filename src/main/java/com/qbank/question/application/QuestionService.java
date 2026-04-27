package com.qbank.question.application;

import com.qbank.bookmark.domain.BookmarkCountProjection;
import com.qbank.bookmark.domain.BookmarkRepository;
import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
import com.qbank.answer.domain.AnswerRepository;
import com.qbank.common.response.SliceResponse;
import com.qbank.question.application.dto.QuestionDetail;
import com.qbank.question.application.dto.QuestionSearchCondition;
import com.qbank.question.application.dto.QuestionStatsResponse;
import com.qbank.question.application.dto.QuestionSummary;
import com.qbank.question.application.dto.RegisterQuestion;
import com.qbank.question.application.dto.StudyQuestionSummary;
import com.qbank.question.domain.*;
import com.qbank.review.domain.ReviewRepository;
import com.qbank.review.domain.ReviewStatus;
import com.qbank.review.domain.UserQuestionReview;
import com.qbank.tag.domain.Tag;
import com.qbank.tag.domain.TagRepository;
import com.qbank.user.domain.User;
import com.qbank.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final ReviewRepository reviewRepository;
    private final AnswerRepository answerRepository;

    public SliceResponse<QuestionSummary.Response> getPublicQuestions(QuestionSummary.Request dto) {
        int size = dto.pageable().getPageSize();
        int page = dto.pageable().getPageNumber();
        Pageable slicePageable = PageRequest.of(page, size + 1, dto.pageable().getSort());

        List<Question> fetched;
        if ("BOOKMARK_COUNT".equals(dto.sort())) {
            fetched = new ArrayList<>(questionRepository.findPublicByBookmarkCount(slicePageable).getContent());
        } else {
            Specification<Question> spec = QuestionSpecification.publicSearch(QuestionSearchCondition.of(dto));
            fetched = new ArrayList<>(questionRepository.findAll(spec, slicePageable).getContent());
        }

        boolean hasNext = fetched.size() > size;
        List<Question> items = hasNext ? fetched.subList(0, size) : fetched;
        return enrichAsSlice(items, hasNext, page, dto.userId());
    }

    public SliceResponse<QuestionSummary.Response> getMyQuestions(Visibility visibility, Pageable pageable, Long userId) {
        int size = pageable.getPageSize();
        Pageable slicePageable = PageRequest.of(pageable.getPageNumber(), size + 1, pageable.getSort());
        List<Question> fetched = new ArrayList<>(
                questionRepository.findAll(QuestionSpecification.myQuestions(userId, visibility), slicePageable).getContent());
        boolean hasNext = fetched.size() > size;
        return enrichAsSlice(hasNext ? fetched.subList(0, size) : fetched, hasNext, pageable.getPageNumber(), userId);
    }

    public SliceResponse<QuestionSummary.Response> getBookmarkedQuestions(Pageable pageable, Long userId) {
        int size = pageable.getPageSize();
        Pageable slicePageable = PageRequest.of(pageable.getPageNumber(), size + 1, pageable.getSort());
        List<Question> fetched = new ArrayList<>(
                questionRepository.findAll(QuestionSpecification.isBookmarkedByUser(userId), slicePageable).getContent());
        boolean hasNext = fetched.size() > size;
        return enrichAsSlice(hasNext ? fetched.subList(0, size) : fetched, hasNext, pageable.getPageNumber(), userId);
    }

    public SliceResponse<QuestionSummary.Response> getFeed(Long userId, Pageable pageable) {
        int size = pageable.getPageSize();
        Pageable slicePageable = PageRequest.of(pageable.getPageNumber(), size + 1);
        List<Question> fetched = new ArrayList<>(
                questionRepository.findFeedQuestions(userId, slicePageable).getContent());
        boolean hasNext = fetched.size() > size;
        return enrichAsSlice(hasNext ? fetched.subList(0, size) : fetched, hasNext, pageable.getPageNumber(), userId);
    }

    private SliceResponse<QuestionSummary.Response> enrichAsSlice(
            List<Question> items, boolean hasNext, int pageNumber, Long userId) {
        if (items.isEmpty()) return SliceResponse.empty(pageNumber);

        List<Long> questionIds = items.stream().map(Question::getId).toList();
        Set<Long> authorIds = items.stream().map(Question::getAuthorId).collect(Collectors.toSet());

        Map<Long, List<QuestionSummary.Response.TagInfo>> tagsByQuestionId = buildTagMap(questionIds);
        Map<Long, String> nicknameByAuthorId = buildNicknameMap(authorIds);
        Map<Long, Long> bookmarkCountByQuestionId = buildBookmarkCountMap(questionIds);
        Set<Long> bookmarkedIds = userId != null
                ? new HashSet<>(bookmarkRepository.findBookmarkedQuestionIds(userId, questionIds))
                : Set.of();

        List<QuestionSummary.Response> responses = QuestionSummary.Response.of(
                items, tagsByQuestionId, nicknameByAuthorId, bookmarkCountByQuestionId, bookmarkedIds, userId);
        return new SliceResponse<>(responses, hasNext, pageNumber);
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

    public SliceResponse<StudyQuestionSummary> getStudyQuestions(ReviewStatus reviewStatus,
                                                                   List<Long> tagIds,
                                                                   Boolean hasAnswer,
                                                                   Boolean isMine,
                                                                   Boolean isBookmarked,
                                                                   Visibility visibility,
                                                                   Pageable pageable,
                                                                   Long userId) {

        int size = pageable.getPageSize();
        Pageable slicePageable = PageRequest.of(pageable.getPageNumber(), size + 1, pageable.getSort());

        Specification<Question> spec = buildStudySpec(userId, reviewStatus, tagIds, hasAnswer, isMine, isBookmarked, visibility);
        List<Question> fetched = new ArrayList<>(questionRepository.findAll(spec, slicePageable).getContent());

        boolean hasNext = fetched.size() > size;
        List<Question> items = hasNext ? fetched.subList(0, size) : fetched;
        if (items.isEmpty()) return SliceResponse.empty(pageable.getPageNumber());

        List<Long> questionIds = items.stream().map(Question::getId).toList();
        Set<Long> authorIds = items.stream().map(Question::getAuthorId).collect(Collectors.toSet());

        Map<Long, List<StudyQuestionSummary.TagInfo>> tagsByQuestionId =
                questionTagRepository.findAllWithTagByQuestionIdIn(questionIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                qt -> qt.getQuestion().getId(),
                                Collectors.mapping(
                                        qt -> new StudyQuestionSummary.TagInfo(qt.getTag().getId(), qt.getTag().getName()),
                                        Collectors.toList()
                                )
                        ));

        Map<Long, String> nicknameByAuthorId = buildNicknameMap(authorIds);
        Map<Long, Long> bookmarkCountByQuestionId = buildBookmarkCountMap(questionIds);
        Set<Long> bookmarkedIds = new HashSet<>(bookmarkRepository.findBookmarkedQuestionIds(userId, questionIds));

        Map<Long, ReviewStatus> reviewStatusByQuestionId = reviewRepository
                .findAllByUserIdAndQuestionIdIn(userId, questionIds)
                .stream()
                .collect(Collectors.toMap(UserQuestionReview::getQuestionId, UserQuestionReview::getStatus));

        Set<Long> answeredIds = new HashSet<>(answerRepository.findAnsweredQuestionIds(userId, questionIds));

        List<StudyQuestionSummary> responses = items.stream().map(q -> new StudyQuestionSummary(
                q.getId(),
                q.getTitle(),
                tagsByQuestionId.getOrDefault(q.getId(), List.of()),
                q.getCareerLevel(),
                q.getDifficulty(),
                q.getVisibility(),
                nicknameByAuthorId.getOrDefault(q.getAuthorId(), "알 수 없음"),
                Objects.equals(q.getAuthorId(), userId),
                bookmarkedIds.contains(q.getId()),
                bookmarkCountByQuestionId.getOrDefault(q.getId(), 0L),
                q.getCreatedAt(),
                reviewStatusByQuestionId.get(q.getId()),
                answeredIds.contains(q.getId())
        )).toList();

        return new SliceResponse<>(responses, hasNext, pageable.getPageNumber());
    }

    private Specification<Question> buildStudySpec(Long userId, ReviewStatus reviewStatus,
                                                    List<Long> tagIds, Boolean hasAnswer,
                                                    Boolean isMine, Boolean isBookmarked,
                                                    Visibility visibility) {
        Specification<Question> spec = QuestionSpecification.isStudyQuestion(userId);
        if (reviewStatus != null)              spec = spec.and(QuestionSpecification.hasReviewStatus(userId, reviewStatus));
        if (tagIds != null && !tagIds.isEmpty()) spec = spec.and(QuestionSpecification.hasAnyTagId(tagIds));
        if (Boolean.TRUE.equals(hasAnswer))    spec = spec.and(QuestionSpecification.hasAnswerWritten(userId));
        if (Boolean.FALSE.equals(hasAnswer))   spec = spec.and(QuestionSpecification.hasNoAnswer(userId));
        if (Boolean.TRUE.equals(isMine))       spec = spec.and(QuestionSpecification.isAuthor(userId));
        if (Boolean.TRUE.equals(isBookmarked)) spec = spec.and(QuestionSpecification.isBookmarkedByUser(userId));
        if (visibility != null)                spec = spec.and(QuestionSpecification.hasVisibility(visibility));
        return spec;
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
        long publicAnswerCount = answerRepository.countByQuestionIdAndIsPublicTrue(questionId);

        return new QuestionDetail.Response(
                question.getId(),
                question.getAuthorId(),
                question.getTitle(),
                tags,
                question.getCareerLevel(),
                question.getDifficulty(),
                question.getVisibility(),
                authorNickname,
                isMyQuestion,
                isBookmarked,
                bookmarkCount,
                publicAnswerCount,
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
                request.getDifficulty(),
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
                request.getDifficulty(),
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
