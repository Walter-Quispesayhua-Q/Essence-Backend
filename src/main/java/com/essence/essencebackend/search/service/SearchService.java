package com.essence.essencebackend.search.service;

import com.essence.essencebackend.search.dto.CategoryDTO;
import com.essence.essencebackend.search.dto.SearchResponseDTO;
import org.schabi.newpipe.extractor.InfoItem;

import java.util.List;

public interface SearchService {
    List<CategoryDTO> getCategories();
    SearchResponseDTO search(String query, String type, int page);

    //metodo para reutilizar en otros modulos
    List<InfoItem> searchByFilter(String query, String filter, int limit);
}