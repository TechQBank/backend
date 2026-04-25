package com.qbank.answer.application;

import com.qbank.answer.application.dto.AnswerHistoryResponse;
import com.qbank.answer.application.dto.AnswerRequest;
import com.qbank.answer.application.dto.AnswerResponse;
import com.qbank.answer.domain.AnswerHistory;
import com.qbank.answer.domain.AnswerHistoryRepository;
import com.qbank.answer.domain.AnswerRepository;
import com.qbank.answer.domain.UserQuestionAnswer;
import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
import com.qbank.question.domain.Question;
import com.qbank.question.domain.QuestionRepository;
import com.qbank.question.domain.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final AnswerHistoryRepository answerHistoryRepository;
    private final QuestionRepository questionRepository;

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
