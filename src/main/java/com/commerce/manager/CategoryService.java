package com.commerce.manager;

import com.commerce.model.Category;
import com.commerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Page<Category> listar(int page, int size) {
        return categoryRepository.findAll(PageRequest.of(page, size));
    }
}
