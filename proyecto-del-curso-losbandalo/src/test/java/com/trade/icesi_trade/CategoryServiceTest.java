package com.trade.icesi_trade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.trade.icesi_trade.model.Category;
import com.trade.icesi_trade.repository.CategoryRepository;
import com.trade.icesi_trade.Service.Impl.CategoryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    
    @InjectMocks
    private CategoryServiceImpl categoryService;
    
    private Category category;
    
    @BeforeEach
    public void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Electrónica")
                .description("Categoría de productos electrónicos")
                .build();
    }
    
    /**
     * Test case for the successful creation of a category.
     * Verifies that the category is saved, the returned object is not null,
     * and the expected name is set correctly.
     */
    @Test
    public void testCreateCategory_Success() {
        // Arrange
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });
        
        // Act
        Category created = categoryService.createCategory(category);
        
        // Assert
        assertNotNull(created);
        assertEquals("Electrónica", created.getName());
        verify(categoryRepository, times(1)).save(category);
    }
    
    /**
     * Tests the behavior of the createCategory method when a null category is provided.
     * Verifies that an IllegalArgumentException is thrown with the expected message.
     */
    @Test
    public void testCreateCategory_NullCategory() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.createCategory(null);
        });
        assertEquals("La categoría no puede ser nula.", exception.getMessage());
    }
    
    /**
     * Test case for the successful update of a category.
     * Verifies that the category is updated correctly and the repository methods
     * are called the expected number of times.
     */
    @Test
    public void testUpdateCategory_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        Category updatedData = Category.builder()
                .name("Computadoras")
                .description("Categoría de computadoras y accesorios")
                .build();
        
        // Act
        Category updated = categoryService.updateCategory(1L, updatedData);
        
        // Assert
        assertNotNull(updated);
        assertEquals("Computadoras", updated.getName());
        assertEquals("Categoría de computadoras y accesorios", updated.getDescription());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
    
    /**
     * Test case for the updateCategory method in the CategoryService class.
     * Verifies that a NoSuchElementException is thrown when attempting to update
     * a category that does not exist in the repository.
     *
     * Scenario:
     * - Given a category ID that does not exist in the repository.
     * - When the updateCategory method is called with the non-existent ID.
     * - Then a NoSuchElementException is expected with a message indicating
     *   that the category was not found.
     */
    @Test
    public void testUpdateCategory_NotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        Category updatedData = Category.builder()
                .name("Computadoras")
                .build();
        
        // Act & Assert
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            categoryService.updateCategory(1L, updatedData);
        });
        assertTrue(exception.getMessage().contains("Categoría no encontrada con el ID: 1"));
    }
    
    /**
     * Test case for the successful deletion of a category.
     * 
     * This test ensures that the deleteCategory method in the CategoryService
     * correctly deletes a category when the category ID exists in the repository.
     * 
     * Steps:
     * 1. Mock the categoryRepository to return true when existsById is called with ID 1.
     * 2. Call the deleteCategory method with ID 1 and assert that the result is true.
     * 3. Verify that the repository's deleteById method is called exactly once.
     */
    @Test
    public void testDeleteCategory_Success() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);
        
        // Act
        boolean deleted = categoryService.deleteCategory(1L);
        
        // Assert
        assertTrue(deleted, "The delete operation should return true for an existing category");
        verify(categoryRepository, times(1)).deleteById(1L);
    }
    
    /**
     * Test case for attempting to delete a category that does not exist.
     * 
     * This test ensures that the deleteCategory method in the CategoryService
     * correctly handles the scenario where the category ID does not exist in the repository.
     * 
     * Steps:
     * 1. Mock the categoryRepository to return false when existsById is called with ID 1.
     * 2. Call the deleteCategory method with ID 1 and assert that the result is false.
     * 3. Verify that the repository's deleteById method is never called.
     */
    @Test
    public void testDeleteCategory_NotFound() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(false);
        
        // Act
        boolean deleted = categoryService.deleteCategory(1L);
        
        // Assert
        assertFalse(deleted, "The delete operation should return false for a non-existent category");
        verify(categoryRepository, never()).deleteById(1L);
    }
    
    /**
     * Test case for the successful retrieval of a category by its ID.
     * 
     * This test verifies that the CategoryService correctly retrieves a category
     * when a valid ID is provided. It ensures that the returned category is not null,
     * has the expected name, and that the repository's findById method is called exactly once.
     */
    @Test
    public void testGetCategoryById_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        
        // Act
        Category found = categoryService.getCategoryById(1L);
        
        // Assert
        assertNotNull(found);
        assertEquals("Electrónica", found.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }
    
    /**
     * Test case for the method getCategoryById in the CategoryService class.
     * 
     * This test verifies the behavior of the method when attempting to retrieve
     * a category by its ID, but the category is not found in the repository.
     * 
     * Steps:
     * 1. Mock the categoryRepository to return an empty Optional when findById is called with ID 1.
     * 2. Call the getCategoryById method with ID 1 and assert that a NoSuchElementException is thrown.
     * 3. Verify that the exception message contains the expected text indicating the category was not found.
     * 
     * Expected Outcome:
     * The method should throw a NoSuchElementException with a message indicating
     * that the category with the specified ID was not found.
     */
    @Test
    public void testGetCategoryById_NotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            categoryService.getCategoryById(1L);
        });
        assertTrue(exception.getMessage().contains("Categoría no encontrada con el ID: 1"));
    }
    
    /**
     * Test for retrieving all categories.
     * Ensures that the service correctly fetches all categories from the repository.
     */
    @Test
    public void testGetAllCategories() {
        // Arrange
        List<Category> categoryList = Arrays.asList(
                category, 
                Category.builder().id(2L).name("Ropa").description("Categoría de ropa").build()
        );
        when(categoryRepository.findAll()).thenReturn(categoryList);
        
        // Act
        List<Category> allCategories = categoryService.getAllCategories();
        
        // Assert
        assertNotNull(allCategories, "The list of categories should not be null");
        assertEquals(2, allCategories.size(), "The size of the category list should be 2");
        verify(categoryRepository, times(1)).findAll();
    }

    /**
     * Verifica que se lance una excepción cuando el nombre de la categoría es null.
     */
    @Test
    public void testCreateCategory_ThrowsException_WhenNameIsNull() {
        category.setName(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.createCategory(category);
        });
        assertEquals("La categoría debe tener un nombre.", exception.getMessage());
    }

    /**
     * Verifica que se lance una excepción cuando el nombre de la categoría está vacío.
     */
    @Test
    public void testCreateCategory_ThrowsException_WhenNameIsEmpty() {
        category.setName("   ");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.createCategory(category);
        });
        assertEquals("La categoría debe tener un nombre.", exception.getMessage());
    }

    /**
     * Verifica que se lance excepción si el ID o categoría es null al actualizar.
     */
    @Test
    public void testUpdateCategory_ThrowsException_WhenIdOrCategoryIsNull() {
        Category valid = Category.builder().name("Ropa").build();

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () ->
            categoryService.updateCategory(null, valid));
        assertEquals("El ID y la categoría actualizada no pueden ser nulos.", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () ->
            categoryService.updateCategory(1L, null));
        assertEquals("El ID y la categoría actualizada no pueden ser nulos.", ex2.getMessage());
    }

    /**
     * Verifica que se lance excepción si el ID es null al eliminar.
     */
    @Test
    public void testDeleteCategory_ThrowsException_WhenIdIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.deleteCategory(null);
        });
        assertEquals("El ID de la categoría no puede ser nulo.", exception.getMessage());
    }

    /**
     * Verifica que se lance excepción si el ID es null al consultar.
     */
    @Test
    public void testGetCategoryById_ThrowsException_WhenIdIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.getCategoryById(null);
        });
        assertEquals("El ID de la categoría no puede ser nulo.", exception.getMessage());
    }
}
