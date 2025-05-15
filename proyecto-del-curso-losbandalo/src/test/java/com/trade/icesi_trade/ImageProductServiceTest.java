package com.trade.icesi_trade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import com.trade.icesi_trade.model.ImageProduct;
import com.trade.icesi_trade.model.Product;
import com.trade.icesi_trade.repository.ImageProductRepository;
import com.trade.icesi_trade.repository.ProductRepository;
import com.trade.icesi_trade.Service.Impl.ImageProductServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ImageProductServiceTest {

    @Mock
    private ImageProductRepository imageProductRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ImageProductServiceImpl imageProductService;

    // Antes de cada test, se inyecta la URL base de imágenes (para simular un entorno desplegado)
    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(imageProductService, "imageBaseUrl", "http://testserver.com/images/");
    }

    @Test
    public void testUploadImage_Success() throws Exception {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "testImage.jpg", "image/jpeg", "dummy content".getBytes());
        Long productId = 1L;
        Product product = Product.builder().id(productId).build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(imageProductRepository.save(any(ImageProduct.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ImageProduct imageProduct = imageProductService.uploadImage(file, productId);

        // Assert
        assertNotNull(imageProduct);
        assertNotNull(imageProduct.getUrl());
        assertTrue(imageProduct.getUrl().startsWith("http://testserver.com/images/"));
        assertEquals(product, imageProduct.getProduct());
    }

    /**
     * Tests the uploadImage method to ensure it throws an IllegalArgumentException
     * when attempting to upload a file that exceeds the size limit of 5 MB.
     * 
     * Scenario:
     * - A file larger than 5 MB is created and passed to the uploadImage method.
     * - The method is expected to throw an exception with a specific error message.
     */
    @Test
    public void testUploadImage_FileTooLarge() {
        // Arrange
        byte[] bigData = new byte[(int) (5 * 1024 * 1024 + 1)];
        MultipartFile file = new MockMultipartFile("file", "bigImage.jpg", "image/jpeg", bigData);
        Long productId = 1L;
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            imageProductService.uploadImage(file, productId)
        );
        assertEquals("El tamaño del archivo excede el límite permitido de 5MB", exception.getMessage());
    }

    /**
     * Tests the uploadImage method of the ImageProductService class to ensure that
     * it throws an IllegalArgumentException when attempting to upload an image with
     * an invalid file extension (e.g., .gif).
     *
     * The test verifies:
     * - That the exception is thrown.
     * - That the exception message contains the expected error message indicating
     *   the unsupported image format.
     */
    @Test
    public void testUploadImage_InvalidExtension() {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "testImage.gif", "image/gif", "dummy content".getBytes());
        Long productId = 1L;
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            imageProductService.uploadImage(file, productId)
        );
        assertTrue(exception.getMessage().contains("Formato de imagen no permitido"));
    }

    /**
     * Tests the uploadImage method of the ImageProductService when the product is not found.
     * 
     * Scenario:
     * - A valid image file is provided.
     * - The product ID does not exist in the repository.
     * 
     * Expected Outcome:
     * - A NoSuchElementException is thrown with a message indicating the product was not found.
     */
    @Test
    public void testUploadImage_ProductNotFound() {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "testImage.jpg", "image/jpeg", "dummy content".getBytes());
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NoSuchElementException.class, () ->
            imageProductService.uploadImage(file, productId)
        );
        assertTrue(exception.getMessage().contains("Producto no encontrado con ID: " + productId));
    }

    /**
     * Tests the getImageByProductId method of the ImageProductService to ensure it
     * successfully retrieves an image associated with a given product ID.
     * 
     * Scenario:
     * - A product ID is provided, and an image exists for that product in the repository.
     * 
     * Expected Outcome:
     * - The method returns the correct ImageProduct object with the expected URL.
     */
    @Test
    public void testGetImageByProductId_Success() {
        // Arrange
        Long productId = 1L;
        ImageProduct imageProduct = ImageProduct.builder()
                .id(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                .url("http://testserver.com/images/test.jpg")
                .build();
        when(imageProductRepository.findByProduct_Id(productId)).thenReturn(imageProduct);

        // Act
        ImageProduct result = imageProductService.getImageByProductId(productId);

        // Assert
        assertNotNull(result);
        assertEquals("http://testserver.com/images/test.jpg", result.getUrl());
    }

    /**
     * Test case for the method getImageByProductId when no image is found for the given product ID.
     * 
     * This test verifies that the service throws a NoSuchElementException with the appropriate
     * error message when the repository returns null for the specified product ID.
     */
    @Test
    public void testGetImageByProductId_NotFound() {
        // Arrange
        Long productId = 1L;
        when(imageProductRepository.findByProduct_Id(productId)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(NoSuchElementException.class, () ->
            imageProductService.getImageByProductId(productId)
        );
        assertTrue(exception.getMessage().contains("No se encontró imagen para el producto con ID: " + productId));
    }

    /**
     * Test case for the deleteImage method in the ImageProductService class.
     * Verifies that the method successfully deletes an image when the image exists.
     * 
     * Steps:
     * 1. Simulates the existence of an image with a given ID.
     * 2. Calls the deleteImage method with the specified image ID.
     * 3. Asserts that the method returns true, indicating successful deletion.
     * 4. Verifies that the repository's deleteById method is called exactly once.
     */
    @Test
    public void testDeleteImage_Success() {
        // Arrange: Simulamos que la imagen existe y puede ser eliminada
        Long imageId = 10L;
        when(imageProductRepository.existsById(imageId)).thenReturn(true);

        // Act
        boolean result = imageProductService.deleteImage(imageId);

        // Assert
        assertTrue(result);
        verify(imageProductRepository, times(1)).deleteById(imageId);
    }

    /**
     * Test case for the deleteImage method in the ImageProductService class.
     * Verifies that the method returns false when attempting to delete a non-existent image.
     * 
     * Steps:
     * 1. Simulates the absence of an image with a given ID.
     * 2. Calls the deleteImage method with the specified image ID.
     * 3. Asserts that the method returns false, indicating the image was not found.
     * 4. Verifies that the repository's deleteById method is never called.
     */
    @Test
    public void testDeleteImage_NotFound() {
        // Arrange
        Long imageId = 10L;
        when(imageProductRepository.existsById(imageId)).thenReturn(false);

        // Act
        boolean result = imageProductService.deleteImage(imageId);

        // Assert
        assertFalse(result);
        verify(imageProductRepository, never()).deleteById(imageId);
    }

    /**
     * Test for uploadImage when the MultipartFile is null.
     */
    @Test
    public void testUploadImage_NullFile() {
        Long productId = 1L;

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            imageProductService.uploadImage(null, productId)
        );
        assertEquals("El archivo no puede ser nulo o estar vacío", exception.getMessage());
    }

    /**
     * Test for uploadImage when the MultipartFile is empty.
     */
    @Test
    public void testUploadImage_EmptyFile() {
        MultipartFile file = new MockMultipartFile("file", "", "image/jpeg", new byte[0]);
        Long productId = 1L;

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            imageProductService.uploadImage(file, productId)
        );
        assertEquals("El archivo no puede ser nulo o estar vacío", exception.getMessage());
    }

    /**
     * Test for uploadImage when the file has no extension.
     */
    @Test
    public void testUploadImage_NoExtension() {
        MultipartFile file = new MockMultipartFile("file", "imagen", "image/jpeg", "content".getBytes());
        Long productId = 1L;

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            imageProductService.uploadImage(file, productId)
        );
        assertEquals("El archivo debe tener una extensión válida", exception.getMessage());
    }

    /**
     * Test for uploadImage when the productId is null.
     */
    @Test
    public void testUploadImage_NullProductId() {
        MultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "data".getBytes());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            imageProductService.uploadImage(file, null)
        );
        assertEquals("El ID del producto no puede ser nulo", exception.getMessage());
    }

    /**
     * Test for getImageByProductId with null ID.
     */
    @Test
    public void testGetImageByProductId_Null() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            imageProductService.getImageByProductId(null)
        );
        assertEquals("El ID del producto no puede ser nulo", exception.getMessage());
    }

    /**
     * Test for deleteImage when imageId is null.
     */
    @Test
    public void testDeleteImage_NullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            imageProductService.deleteImage(null)
        );
        assertEquals("El ID de la imagen no puede ser nulo", exception.getMessage());
    }
}