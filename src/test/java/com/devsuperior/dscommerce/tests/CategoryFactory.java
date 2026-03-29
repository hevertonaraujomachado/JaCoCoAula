package com.devsuperior.dscommerce.tests;

import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;

public class CategoryFactory {




    public static Category createCategory() {
        return new Category(1L, "Games");

    }

    public static Category createCategory(Long id, String name) {
        return new Category(id, name);

    }
}