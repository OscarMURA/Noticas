package com.trade.icesi_trade.Service.Interface;

import com.trade.icesi_trade.model.Category;
import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);

    Category updateCategory(Long id, Category category);

    boolean deleteCategory(Long id);

    Category getCategoryById(Long id);

    List<Category> getAllCategories();
}
