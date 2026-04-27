package com.qbank.answer.application;

import com.qbank.answer.application.dto.*;
import com.qbank.answer.domain.*;
import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
import com.qbank.question.domain.Question;
import com.qbank.question.domain.QuestionRepository;
import com.qbank.question.domain.Visibility;
import com.qbank.user.domain.User;
import com.qbank.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final AnswerHistoryRepository answerHistoryRepository;
    private final AnswerLikeRepository answerLikeRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public AnswerResponse getAnswer(Long questionId, Long userId) {
        validateAccess(questionId, userId);
        return answerRepository.findByUserIdAndQuestionId(userId, questionId)
                .map(AnswerResponse::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));
    }

    @Transactional
    public AnswerResponse createAnswer(Long questionId, AnswerRequest request, Long userId) {
        validateAccess(questionId, userId);

        if (answerRepository.existsByUserIdAndQuestionId(userId, questionId)) {
            throw new BusinessException(ErrorCode.ANSWER_ALREADY_EXISTS);
        }

        UserQuestionAnswer answer = UserQuestionAnswer.create(userId, questionId, request.content());
        UserQuestionAnswer saved = answerRepository.save(answer);

        answerHistoryRepository.save(AnswerHistory.of(saved, request.content(), 1));

        return AnswerResponse.from(saved);
    }

    @Transactional
    public AnswerResponse updateAnswer(Long questionId, AnswerRequest request, Long userId) {
        validateAccess(questionId, userId);

        UserQuestionAnswer answer = answerRepository.findByUserIdAndQuestionId(userId, questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));

        answer.updateContent(request.content());
        answerHistoryRepository.save(AnswerHistory.of(answer, request.content(), answer.getVersion()));

        return AnswerResponse.from(answer);
    }

    @Transactional
    public AnswerResponse togglePublic(Long questionId, boolean isPublic, Long userId) {
        validateAccess(questionId, userId);

        UserQuestionAnswer answer = answerRepository.findByUserIdAndQuestionId(userId, questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));

        answer.togglePublic(isPublic);
        return AnswerResponse.from(answer);
    }

    public List<PublicAnswerResponse> getPublicAnswers(Long questionId, Long userId) {
        List<UserQuestionAnswer> answers = answerRepository.findPublicAnswersByQuestionId(questionId);

        Set<Long> authorIds = answers.stream().map(UserQuestionAnswer::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = userRepository.findAllById(authorIds)
                .stream().collect(Collectors.toMap(User::getId, u -> u));

        return answers.stream()
                .filter(a -> !Objects.equals(a.getUserId(), userId))
                .map(a -> {
                    long likeCount = answerLikeRepository.countByAnswerId(a.getId());
                    boolean isLiked = userId != null && answerLikeRepository.existsByAnswerIdAndUserId(a.getId(), userId);
                    return PublicAnswerResponse.of(a, userMap.get(a.getUserId()), likeCount, isLiked, userId);
                })
                .toList();
    }

    @Transactional
    public LikeResponse likeAnswer(Long answerId, Long userId) {
        answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));

        if (!answerLikeRepository.existsByAnswerIdAndUserId(answerId, userId)) {
            answerLikeRepository.save(AnswerLike.of(answerId, userId));
        }
        return new LikeResponse(answerLikeRepository.countByAnswerId(answerId), true);
    }

    @Transactional
    public LikeResponse unlikeAnswer(Long answerId, Long userId) {
        answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));

        if (answerLikeRepository.existsByAnswerIdAndUserId(answerId, userId)) {
            answerLikeRepository.deleteByAnswerIdAndUserId(answerId, userId);
        }
        return new LikeResponse(answerLikeRepository.countByAnswerId(answerId), false);
    }

    public List<AnswerHistoryResponse> getHistory(Long questionId, Long userId) {
        validateAccess(questionId, userId);

        UserQuestionAnswer answer = answerRepository.findByUserIdAndQuestionId(userId, questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));

        return answerHistoryRepository.findAllByAnswerIdOrderByVersionDesc(answer.getId())
                .stream()
                .map(AnswerHistoryResponse::from)
                .toList();
    }

    private void validateAccess(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
        if (question.getVisibility() == Visibility.PRIVATE
                && !Objects.equals(question.getAuthorId(), userId)) {
            throw new BusinessException(ErrorCode.QUESTION_NOT_FOUND);
        }
    }
}
