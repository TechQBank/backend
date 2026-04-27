package com.qbank.tag.presentation;

import com.qbank.tag.application.TagService;
import com.qbank.tag.application.dto.CreateTagRequest;
import com.qbank.tag.application.dto.PopularTagResponse;
import com.qbank.tag.application.dto.TagResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public List<TagResponse> getAllTags(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return tagService.findByKeyword(keyword);
        }
        return tagService.getAllTags();
    }

    @PostMapping
    public ResponseEntity<TagResponse> createTag(@RequestBody @Valid CreateTagRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.createOrGet(request.name()));
    }

    @GetMapping("/popular")
    public List<PopularTagResponse> getPopularTags(
            @RequestParam(defaultValue = "10") int limit) {
        return tagService.getPopularTags(limit);
    }
}
