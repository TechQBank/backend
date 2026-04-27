package com.qbank.group.presentation;

import com.qbank.auth.SecurityUtils;
import com.qbank.group.application.GroupService;
import com.qbank.group.application.dto.AddQuestionRequest;
import com.qbank.group.application.dto.GroupRequest;
import com.qbank.group.application.dto.GroupResponse;
import com.qbank.question.application.dto.StudyQuestionSummary;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public List<GroupResponse> getMyGroups() {
        return groupService.getMyGroups(SecurityUtils.getCurrentUserId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse createGroup(@Valid @RequestBody GroupRequest request) {
        return groupService.createGroup(request, SecurityUtils.getCurrentUserId());
    }

    @GetMapping("/{id}")
    public GroupResponse getGroup(@PathVariable Long id) {
        return groupService.getGroup(id, SecurityUtils.getCurrentUserId());
    }

    @PutMapping("/{id}")
    public GroupResponse updateGroup(@PathVariable Long id, @Valid @RequestBody GroupRequest request) {
        return groupService.updateGroup(id, request, SecurityUtils.getCurrentUserId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id, SecurityUtils.getCurrentUserId());
    }

    @GetMapping("/{id}/study-questions")
    public List<StudyQuestionSummary> getGroupStudyQuestions(@PathVariable Long id) {
        return groupService.getGroupStudyQuestions(id, SecurityUtils.getCurrentUserId());
    }

    @PostMapping("/{id}/questions")
    public void addQuestion(@PathVariable Long id, @RequestBody AddQuestionRequest request) {
        groupService.addQuestion(id, request, SecurityUtils.getCurrentUserId());
    }

    @DeleteMapping("/{id}/questions/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeQuestion(@PathVariable Long id, @PathVariable Long questionId) {
        groupService.removeQuestion(id, questionId, SecurityUtils.getCurrentUserId());
    }

    @GetMapping("/containing/{questionId}")
    public List<Long> getGroupIdsContaining(@PathVariable Long questionId) {
        return groupService.getGroupIdsContaining(questionId, SecurityUtils.getCurrentUserId());
    }
}
