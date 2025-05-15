package com.trade.icesi_trade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import com.trade.icesi_trade.model.FavoriteProduct;
import com.trade.icesi_trade.model.Product;
import com.trade.icesi_trade.model.User;
import com.trade.icesi_trade.repository.FavoriteProductRepository;
import com.trade.icesi_trade.repository.ProductRepository;
import com.trade.icesi_trade.repository.UserRepository;
import com.trade.icesi_trade.Service.Impl.FavoriteProductServiceImpl;

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
public class FavoriteProductServiceTest {

    @Mock
    private FavoriteProductRepository favoriteProductRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private FavoriteProductServiceImpl favoriteProductService;

    private User user;
    private Product product;
    private FavoriteProduct favoriteProduct;

    @BeforeEach
    public void setUp() {
        // Se crea un usuario y un producto de prueba.
        user = User.builder()
                .id(1L)
                .build();
        product = Product.builder()
                .id(100L)
                .build();
        // Se crea un FavoriteProduct asociado al usuario y producto.
        favoriteProduct = FavoriteProduct.builder()
                .id(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void testAddFavoriteProduct_Success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(favoriteProductRepository.findByProduct_IdAndUser_Id(product.getId(), user.getId())).thenReturn(null);
        when(favoriteProductRepository.save(any(FavoriteProduct.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        FavoriteProduct result = favoriteProductService.addFavoriteProduct(user.getId(), product.getId());

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(product, result.getProduct());
        verify(userRepository, times(1)).findById(user.getId());
        verify(productRepository, times(1)).findById(product.getId());
        verify(favoriteProductRepository, times(1)).save(any(FavoriteProduct.class));
    }

    @Test
    public void testAddFavoriteProduct_AlreadyExists() {
        when(favoriteProductRepository.findByProduct_IdAndUser_Id(product.getId(), user.getId()))
                .thenReturn(favoriteProduct);

        // Act & Assert: Se debe lanzar excepción indicando que ya existe.
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                favoriteProductService.addFavoriteProduct(user.getId(), product.getId())
        );
        assertTrue(exception.getMessage().contains("El producto ya está marcado como favorito para este usuario."));
    }

    @Test
    public void testAddFavoriteProduct_UserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // Act & Assert: Se lanza NoSuchElementException.
        Exception exception = assertThrows(NoSuchElementException.class, () ->
                favoriteProductService.addFavoriteProduct(user.getId(), product.getId())
        );
        assertTrue(exception.getMessage().contains("Usuario no encontrado con ID: " + user.getId()));
    }

    @Test
    public void testAddFavoriteProduct_ProductNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        // Act & Assert: Se lanza NoSuchElementException.
        Exception exception = assertThrows(NoSuchElementException.class, () ->
                favoriteProductService.addFavoriteProduct(user.getId(), product.getId())
        );
        assertTrue(exception.getMessage().contains("Producto no encontrado con ID: " + product.getId()));
    }

    @Test
    public void testRemoveFavoriteProduct_Success() {
        when(favoriteProductRepository.findByProduct_IdAndUser_Id(product.getId(), user.getId()))
                .thenReturn(favoriteProduct);

        // Act
        boolean result = favoriteProductService.removeFavoriteProduct(user.getId(), product.getId());

        // Assert
        assertTrue(result);
        verify(favoriteProductRepository, times(1)).delete(favoriteProduct);
    }

    @Test
    public void testRemoveFavoriteProduct_NotFound() {
        when(favoriteProductRepository.findByProduct_IdAndUser_Id(product.getId(), user.getId()))
                .thenReturn(null);

        // Act
        boolean result = favoriteProductService.removeFavoriteProduct(user.getId(), product.getId());

        // Assert
        assertFalse(result);
        verify(favoriteProductRepository, never()).delete(any(FavoriteProduct.class));
    }

    @Test
    public void testGetFavoriteProduct_Success() {
        when(favoriteProductRepository.findByProduct_IdAndUser_Id(product.getId(), user.getId()))
                .thenReturn(favoriteProduct);

        // Act
        FavoriteProduct result = favoriteProductService.getFavoriteProduct(user.getId(), product.getId());

        // Assert
        assertNotNull(result);
        assertEquals(favoriteProduct.getId(), result.getId());
    }

    @Test
    public void testGetFavoriteProduct_NotFound() {
        when(favoriteProductRepository.findByProduct_IdAndUser_Id(product.getId(), user.getId()))
                .thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(NoSuchElementException.class, () ->
                favoriteProductService.getFavoriteProduct(user.getId(), product.getId())
        );
        assertTrue(exception.getMessage().contains("No se encontró producto favorito para el usuario"));
    }

    @Test
    public void testGetFavoriteProductsByUser() {
        FavoriteProduct anotherFavorite = FavoriteProduct.builder()
                .id(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                .user(user)
                .product(Product.builder().id(200L).build())
                .createdAt(LocalDateTime.now())
                .build();
        List<FavoriteProduct> favoritesList = Arrays.asList(favoriteProduct, anotherFavorite);
        when(favoriteProductRepository.findAll()).thenReturn(favoritesList);

        // Act
        List<FavoriteProduct> result = favoriteProductService.getFavoriteProductsByUser(user.getId());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        for (FavoriteProduct fp : result) {
            assertEquals(user.getId(), fp.getUser().getId());
        }
        verify(favoriteProductRepository, times(1)).findAll();
    }

    @Test
        public void testAddFavoriteProduct_NullIds() {
        Exception ex1 = assertThrows(IllegalArgumentException.class, () ->
                favoriteProductService.addFavoriteProduct(null, 100L));
        assertEquals("El ID del usuario y del producto no pueden ser nulos.", ex1.getMessage());

        Exception ex2 = assertThrows(IllegalArgumentException.class, () ->
                favoriteProductService.addFavoriteProduct(1L, null));
        assertEquals("El ID del usuario y del producto no pueden ser nulos.", ex2.getMessage());
        }

        @Test
        public void testRemoveFavoriteProduct_NullIds() {
        Exception ex1 = assertThrows(IllegalArgumentException.class, () ->
                favoriteProductService.removeFavoriteProduct(null, 100L));
        assertEquals("El ID del usuario y del producto no pueden ser nulos.", ex1.getMessage());

        Exception ex2 = assertThrows(IllegalArgumentException.class, () ->
                favoriteProductService.removeFavoriteProduct(1L, null));
        assertEquals("El ID del usuario y del producto no pueden ser nulos.", ex2.getMessage());
        }

        @Test
        public void testGetFavoriteProduct_NullIds() {
        Exception ex1 = assertThrows(IllegalArgumentException.class, () ->
                favoriteProductService.getFavoriteProduct(null, 100L));
        assertEquals("El ID del usuario y del producto no pueden ser nulos.", ex1.getMessage());

        Exception ex2 = assertThrows(IllegalArgumentException.class, () ->
                favoriteProductService.getFavoriteProduct(1L, null));
        assertEquals("El ID del usuario y del producto no pueden ser nulos.", ex2.getMessage());
        }

        @Test
        public void testGetFavoriteProductsByUser_NullId() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                favoriteProductService.getFavoriteProductsByUser(null));
        assertEquals("El ID del usuario no puede ser nulo.", ex.getMessage());
        }
}
