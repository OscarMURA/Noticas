package com.trade.icesi_trade.Service.Impl;

import com.trade.icesi_trade.Service.Interface.CategoryService;
import com.trade.icesi_trade.model.Category;
import com.trade.icesi_trade.repository.CategoryRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Creates a new category in the system.
     *
     * @param category The category object to be created. Must not be null and must have a valid name.
     * @return The created category object after being saved in the repository.
     * @throws IllegalArgumentException If the category is null or if the category's name is null or empty.
     */
    @Override
    public Category createCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("La categoría no puede ser nula.");
        }
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría debe tener un nombre.");
        }
        return categoryRepository.save(category);
    }

    /**
     * Updates an existing category with the provided details.
     *
     * @param id The ID of the category to be updated. Must not be null.
     * @param category The updated category details. Must not be null.
     * @return The updated category after saving it to the repository.
     * @throws IllegalArgumentException If the ID or the category is null.
     * @throws NoSuchElementException If no category is found with the given ID.
     */
    @Override
    public Category updateCategory(Long id, Category category) {
        if (id == null || category == null) {
            throw new IllegalArgumentException("El ID y la categoría actualizada no pueden ser nulos.");
        }
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada con el ID: " + id));
        
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        return categoryRepository.save(existingCategory);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id the ID of the category to be deleted; must not be null.
     * @return {@code true} if the category was successfully deleted, {@code false} if the category does not exist.
     * @throws IllegalArgumentException if the provided ID is null.
     */
    @Override
    public boolean deleteCategory(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la categoría no puede ser nulo.");
        }
        if (!categoryRepository.existsById(id)) {
            return false;
        }
        categoryRepository.deleteById(id);
        return true;
    }

    /**
     * Retrieves a category by its unique identifier.
     *
     * @param id the unique identifier of the category to retrieve; must not be null.
     * @return the Category object associated with the given ID.
     * @throws IllegalArgumentException if the provided ID is null.
     * @throws NoSuchElementException if no category is found with the given ID.
     */
    @Override
    public Category getCategoryById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la categoría no puede ser nulo.");
        }
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada con el ID: " + id));
    }

    /**
     * Retrieves a list of all categories from the repository.
     *
     * @return a list of {@link Category} objects representing all categories.
     */
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
