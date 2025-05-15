package com.trade.icesi_trade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.trade.icesi_trade.model.Review;
import com.trade.icesi_trade.model.Product;
import com.trade.icesi_trade.model.User;
import com.trade.icesi_trade.repository.ReviewRepository;
import com.trade.icesi_trade.Service.Impl.ReviewServiceImpl;

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
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review review;
    private Product product;
    private User reviewer;
    private User reviewee;

    @BeforeEach
    public void setUp() {
        reviewer = User.builder().id(1L).build();
        reviewee = User.builder().id(2L).build();
        product = Product.builder().id(100L).build();

        review = Review.builder()
                .id(10L)
                .rating(4)
                .comment("Muy bueno")
                .product(product)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .build();
    }

    @Test
    public void testCreateReview_Success() {
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review r = invocation.getArgument(0);
            r.setId(10L);
            r.setCreatedAt(LocalDateTime.now());
            return r;
        });

        Review created = reviewService.createReview(review);
        assertNotNull(created);
        assertNotNull(created.getCreatedAt());
        assertEquals(4, created.getRating());
        assertEquals("Muy bueno", created.getComment());
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    public void testCreateReview_NullReview() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            reviewService.createReview(null)
        );
        assertEquals("La reseña no puede ser nula.", exception.getMessage());
    }

    @Test
    public void testCreateReview_MissingRating() {
        review.setRating(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            reviewService.createReview(review)
        );
        assertEquals("La reseña debe tener una calificación.", exception.getMessage());
    }

    @Test
    public void testCreateReview_MissingComment() {
        review.setComment("  ");
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            reviewService.createReview(review)
        );
        assertEquals("La reseña debe tener un comentario.", exception.getMessage());
    }

    @Test
    public void testUpdateReview_Success() {
        Review updatedData = Review.builder()
                .rating(5)
                .comment("Excelente producto")
                .build();

        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review updated = reviewService.updateReview(10L, updatedData);
        assertNotNull(updated);
        assertEquals(5, updated.getRating());
        assertEquals("Excelente producto", updated.getComment());
        verify(reviewRepository, times(1)).findById(10L);
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    public void testUpdateReview_NotFound() {
        when(reviewRepository.findById(10L)).thenReturn(Optional.empty());
        Review updatedData = Review.builder()
                .rating(3)
                .comment("Regular")
                .build();

        Exception exception = assertThrows(NoSuchElementException.class, () ->
            reviewService.updateReview(10L, updatedData)
        );
        assertTrue(exception.getMessage().contains("Reseña no encontrada con el ID: 10"));
    }

    // Prueba la eliminación exitosa de una reseña existente
    @Test
    public void testDeleteReview_Success() {
        when(reviewRepository.existsById(10L)).thenReturn(true);
        // No lanza excepción al borrar; el método deleteReview es void
        reviewService.deleteReview(10L);
        verify(reviewRepository, times(1)).deleteById(10L);
    }

    // Prueba la eliminación cuando la reseña no existe
    @Test
    public void testDeleteReview_NotFound() {
        when(reviewRepository.existsById(10L)).thenReturn(false);
        Exception exception = assertThrows(NoSuchElementException.class, () ->
            reviewService.deleteReview(10L)
        );
        assertTrue(exception.getMessage().contains("Reseña no encontrada con el ID: 10"));
    }

    // Prueba la obtención de reseñas por producto
    @Test
    public void testGetReviewsByProduct() {
        List<Review> reviews = Arrays.asList(review);
        when(reviewRepository.findByProduct_Id(product.getId())).thenReturn(reviews);
        List<Review> result = reviewService.getReviewsByProduct(product.getId());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(review.getId(), result.get(0).getId());
        verify(reviewRepository, times(1)).findByProduct_Id(product.getId());
    }

    // Prueba la obtención de reseñas por reviewer
    @Test
    public void testGetReviewsByReviewer() {
        List<Review> reviews = Arrays.asList(review);
        when(reviewRepository.findByReviewer_Id(reviewer.getId())).thenReturn(reviews);
        List<Review> result = reviewService.getReviewsByReviewer(reviewer.getId());
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByReviewer_Id(reviewer.getId());
    }

    // Prueba la obtención de reseñas por reviewee
    @Test
    public void testGetReviewsByReviewee() {
        List<Review> reviews = Arrays.asList(review);
        when(reviewRepository.findByReviewee_Id(reviewee.getId())).thenReturn(reviews);
        List<Review> result = reviewService.getReviewsByReviewee(reviewee.getId());
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByReviewee_Id(reviewee.getId());
    }

    // Prueba el cálculo del promedio de rating para un producto
    @Test
    public void testCalculateAverageRatingByProduct_Success() {
        // Simula un promedio calculado (por ejemplo, 4.5)
        when(reviewRepository.findAverageRatingByProductId(product.getId())).thenReturn(4.5);
        Double average = reviewService.calculateAverageRatingByProduct(product.getId());
        assertNotNull(average);
        assertEquals(4.5, average);
        verify(reviewRepository, times(1)).findAverageRatingByProductId(product.getId());
    }

    // Prueba el cálculo del promedio cuando no hay reseñas
    @Test
    public void testCalculateAverageRatingByProduct_NoReviews() {
        when(reviewRepository.findAverageRatingByProductId(product.getId())).thenReturn(null);
        Double average = reviewService.calculateAverageRatingByProduct(product.getId());
        assertNotNull(average);
        assertEquals(0.0, average);
    }

    @Test
    public void testUpdateReview_NullId() {
        Review updated = Review.builder().rating(4).comment("Test").build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            reviewService.updateReview(null, updated)
        );

        assertEquals("El ID de la reseña y los datos de actualización no pueden ser nulos.", ex.getMessage());
    }

    @Test
    public void testUpdateReview_NullReview() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            reviewService.updateReview(10L, null)
        );

        assertEquals("El ID de la reseña y los datos de actualización no pueden ser nulos.", ex.getMessage());
    }

    @Test
    public void testDeleteReview_NullId() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            reviewService.deleteReview(null)
        );
    
        assertEquals("El ID de la reseña no puede ser nulo.", ex.getMessage());
    }
    
    @Test
    public void testGetReviewsByProduct_NullId() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            reviewService.getReviewsByProduct(null)
        );
    
        assertEquals("El ID del producto no puede ser nulo.", ex.getMessage());
    }

    @Test
    public void testGetReviewsByReviewee_NullId() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            reviewService.getReviewsByReviewee(null)
        );

        assertEquals("El ID del reviewee no puede ser nulo.", ex.getMessage());
    }

    @Test
    public void testCalculateAverageRatingByProduct_NullId() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            reviewService.calculateAverageRatingByProduct(null)
        );

        assertEquals("El ID del producto no puede ser nulo.", ex.getMessage());
    }

    @Test
    public void testGetReviewsByReviewer_NullId() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            reviewService.getReviewsByReviewer(null)
        );
        assertEquals("El ID del revisor no puede ser nulo.", ex.getMessage());
    }
}
