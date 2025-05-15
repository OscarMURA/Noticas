package com.trade.icesi_trade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.trade.icesi_trade.model.Product;
import com.trade.icesi_trade.repository.ProductRepository;
import com.trade.icesi_trade.Service.Impl.ProductServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductServiceImpl productService;
    
    private Product product;

    @BeforeEach
    public void setUp() {
        // Inicializa un producto de ejemplo
        product = Product.builder()
                .id(1L)
                .title("Producto de prueba")
                .description("Descripción de prueba")
                .price(100.0)
                .location("Ubicación de prueba")
                .status("Disponible")
                .build();
    }
    
    @Test
    public void testCreateProduct_Success() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });
        
        // Act
        Product created = productService.createProduct(product);
        
        // Assert
        assertNotNull(created);
        assertEquals("Producto de prueba", created.getTitle());
        assertNotNull(created.getCreatedAt());
        verify(productRepository, times(1)).save(product);
    }
    
    /**
     * Test case for the ProductService's createProduct method when the input product is null.
     * 
     * This test verifies that the method throws an IllegalArgumentException with
     * an appropriate error message when a null product is passed.
     */
    @Test
    public void testCreateProduct_NullProduct() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(null);
        });
        assertEquals("El producto no puede ser nulo.", exception.getMessage());
    }
    
    /**
     * Test case for the successful update of a product.
     * 
     * This test verifies that the `updateProduct` method in the `ProductService` class
     * correctly updates an existing product with new data and persists the changes.
     * 
     * The test performs the following steps:
     * 1. Mocks the `productRepository.findById` method to return an existing product.
     * 2. Mocks the `productRepository.save` method to return the updated product.
     * 3. Creates a `Product` object with updated data.
     * 4. Calls the `updateProduct` method with the product ID and updated data.
     * 5. Asserts that the returned product contains the updated information.
     * 6. Verifies that the `findById` and `save` methods of the repository are called once.
     */
    @Test
    public void testUpdateProduct_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        Product updatedData = Product.builder()
                .title("Producto actualizado")
                .description("Descripción actualizada")
                .price(150.0)
                .location("Nueva ubicación")
                .status("Vendido")
                .build();
        
        // Act
        Product updated = productService.updateProduct(1L, updatedData);
        
        // Assert
        assertNotNull(updated);
        assertEquals("Producto actualizado", updated.getTitle());
        assertEquals("Descripción actualizada", updated.getDescription());
        assertEquals(150.0, updated.getPrice());
        assertEquals("Nueva ubicación", updated.getLocation());
        assertEquals("Vendido", updated.getStatus());
        assertNotNull(updated.getUpdatedAt());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    /**
     * Test case for the ProductService's updateProduct method when the product
     * with the specified ID is not found in the repository.
     *
     * This test verifies that the method throws a NoSuchElementException with
     * an appropriate error message when the product ID does not exist.
     */
    @Test
    public void testUpdateProduct_NotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        Product updatedData = Product.builder()
                .title("Producto actualizado")
                .build();
        
        // Act & Assert
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productService.updateProduct(1L, updatedData);
        });
        assertTrue(exception.getMessage().contains("Producto no encontrado con el ID: 1"));
    }
    
    /**
     * Test case for the ProductService's deleteProduct method when the product
     * with the specified ID exists in the repository.
     *
     * This test verifies that the method returns true and that the repository's
     * deleteById method is called exactly once with the correct ID.
     */
    @Test
    public void testDeleteProduct_Success() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        
        // Act
        boolean result = productService.deleteProduct(1L);
        
        // Assert
        assertTrue(result);
        verify(productRepository, times(1)).deleteById(1L);
    }
    
    /**
     * Test case for the ProductService's deleteProduct method when the product
     * with the specified ID is not found in the repository.
     *
     * This test verifies that the method returns false and that the repository's
     * deleteById method is never called.
     */
    @Test
    public void testDeleteProduct_NotFound() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(false);
        
        // Act
        boolean result = productService.deleteProduct(1L);
        
        // Assert
        assertFalse(result);
        verify(productRepository, never()).deleteById(1L);
    }
    
    /**
     * Test case for the ProductService's getProductById method when the product
     * with the specified ID is found in the repository.
     *
     * This test verifies that the method retrieves the correct product and that
     * the repository's findById method is called exactly once with the correct ID.
     */
    @Test
    public void testGetProductById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        
        // Act
        Product found = productService.getProductById(1L);
        
        // Assert
        assertNotNull(found);
        assertEquals("Producto de prueba", found.getTitle());
        verify(productRepository, times(1)).findById(1L);
    }
    
    /**
     * Test case for the ProductService's getProductById method when the product
     * with the specified ID is not found in the repository.
     *
     * This test verifies that the method throws a NoSuchElementException with
     * an appropriate error message when the product ID does not exist.
     */
    @Test
    public void testGetProductById_NotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productService.getProductById(1L);
        });
        assertTrue(exception.getMessage().contains("Producto no encontrado con el ID: 1"));
    }
    
    /**
     * Test case for the getAllProducts method in the ProductService class.
     * 
     * This test verifies that the method retrieves all products from the repository,
     * ensuring the returned list is not null, contains the expected number of products,
     * and that the repository's findAll method is called exactly once.
     */
    @Test
    public void testGetAllProducts() {
        // Arrange
        List<Product> productList = Arrays.asList(product, product);
        when(productRepository.findAll()).thenReturn(productList);
        
        // Act
        List<Product> allProducts = productService.getAllProducts();
        
        // Assert
        assertNotNull(allProducts);
        assertEquals(2, allProducts.size());
        verify(productRepository, times(1)).findAll();
    }
    
    /**
     * Test case for the ProductService's getAllProducts method with pagination.
     * 
     * This test verifies that the method correctly retrieves a paginated list of products
     * from the repository and returns it as a Page object. It ensures that:
     * - The returned Page object is not null.
     * - The total number of elements in the Page matches the expected count.
     * - The productRepository's findAll method is called exactly once with the correct Pageable argument.
     * 
     * Test scenario:
     * - A Pageable object is created with a page size of 10.
     * - A mock Page object is created containing a list of two Product objects.
     * - The productRepository's findAll method is mocked to return the mock Page object.
     * - The ProductService's getAllProducts method is called with the Pageable object.
     * - Assertions are made to validate the behavior and interactions.
     */
    @Test
    public void testGetAllProductsPaged() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> productList = Arrays.asList(product, product);
        Page<Product> page = new PageImpl<>(productList, pageable, productList.size());
        when(productRepository.findAll(pageable)).thenReturn(page);
        
        // Act
        Page<Product> resultPage = productService.getAllProducts(pageable);
        
        // Assert
        assertNotNull(resultPage);
        assertEquals(2, resultPage.getTotalElements());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testCreateProduct_EmptyTitle() {
        product.setTitle("");
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            productService.createProduct(product)
        );
        assertEquals("El producto debe tener un título.", exception.getMessage());
    }

    @Test
    public void testUpdateProduct_NullId() {
        Product updatedData = Product.builder().title("Nuevo título").build();

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            productService.updateProduct(null, updatedData)
        );
        assertEquals("El ID del producto y los datos a actualizar no pueden ser nulos.", exception.getMessage());
    }

    @Test
    public void testUpdateProduct_NullProduct() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            productService.updateProduct(1L, null)
        );
        assertEquals("El ID del producto y los datos a actualizar no pueden ser nulos.", exception.getMessage());
    }

    @Test
    public void testDeleteProduct_NullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            productService.deleteProduct(null)
        );
        assertEquals("El ID del producto no puede ser nulo.", exception.getMessage());
    }

    @Test
    public void testGetProductById_NullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            productService.getProductById(null)
        );
        assertEquals("El ID del producto no puede ser nulo.", exception.getMessage());
    }
}
