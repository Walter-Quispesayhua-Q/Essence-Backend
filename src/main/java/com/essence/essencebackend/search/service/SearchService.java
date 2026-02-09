package com.essence.essencebackend.search.service;

import com.essence.essencebackend.search.dto.CategoryDTO;
import com.essence.essencebackend.search.dto.SearchResponseDTO;

import java.util.List;

public interface SearchService {
    List<CategoryDTO> getCategories();
    SearchResponseDTO search(String query, String type);
}
