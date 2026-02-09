package com.essence.essencebackend.search.controller;

import com.essence.essencebackend.search.dto.CategoryDTO;
import com.essence.essencebackend.search.dto.SearchResponseDTO;
import com.essence.essencebackend.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        return ResponseEntity.ok(searchService.getCategories());
    }

    @GetMapping
    public ResponseEntity<SearchResponseDTO> search(
            @RequestParam String query,
            @RequestParam(required = false) String type
    ) {
        return ResponseEntity.ok(searchService.search(query, type));
    }
}