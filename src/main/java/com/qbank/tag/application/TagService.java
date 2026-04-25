package com.qbank.tag.application;

import com.qbank.tag.application.dto.TagResponse;
import com.qbank.tag.domain.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;

    public List<TagResponse> getAllTags() {
        return tagRepository.findAllByOrderByNameAsc()
                .stream()
                .map(TagResponse::from)
                .toList();
    }
}
