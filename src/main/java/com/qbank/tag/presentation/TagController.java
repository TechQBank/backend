package com.qbank.tag.presentation;

import com.qbank.tag.application.TagService;
import com.qbank.tag.application.dto.PopularTagResponse;
import com.qbank.tag.application.dto.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public List<TagResponse> getAllTags() {
        return tagService.getAllTags();
    }

    @GetMapping("/popular")
    public List<PopularTagResponse> getPopularTags(
            @RequestParam(defaultValue = "10") int limit) {
        return tagService.getPopularTags(limit);
    }
}
