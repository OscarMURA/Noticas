package com.trade.icesi_trade.Service.Impl;

import com.trade.icesi_trade.Service.Interface.ReviewService;
import com.trade.icesi_trade.model.Review;
import com.trade.icesi_trade.repository.ReviewRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public Review createReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("La reseña no puede ser nula.");
        }
        if (review.getRating() == null) {
            throw new IllegalArgumentException("La reseña debe tener una calificación.");
        }
        if (review.getComment() == null || review.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("La reseña debe tener un comentario.");
        }
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Long reviewId, Review review) {
        if (reviewId == null || review == null) {
            throw new IllegalArgumentException("El ID de la reseña y los datos de actualización no pueden ser nulos.");
        }
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("Reseña no encontrada con el ID: " + reviewId));
        existingReview.setRating(review.getRating());
        existingReview.setComment(review.getComment());
        return reviewRepository.save(existingReview);
    }

    @Override
    public void deleteReview(Long reviewId) {
        if (reviewId == null) {
            throw new IllegalArgumentException("El ID de la reseña no puede ser nulo.");
        }
        if (!reviewRepository.existsById(reviewId)) {
            throw new NoSuchElementException("Reseña no encontrada con el ID: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public List<Review> getReviewsByProduct(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo.");
        }
        return reviewRepository.findByProduct_Id(productId);
    }

    @Override
    public List<Review> getReviewsByReviewer(Long reviewerId) {
        if (reviewerId == null) {
            throw new IllegalArgumentException("El ID del revisor no puede ser nulo.");
        }
        return reviewRepository.findByReviewer_Id(reviewerId);
    }

    @Override
    public List<Review> getReviewsByReviewee(Long revieweeId) {
        if (revieweeId == null) {
            throw new IllegalArgumentException("El ID del reviewee no puede ser nulo.");
        }
        return reviewRepository.findByReviewee_Id(revieweeId);
    }

    @Override
    public Double calculateAverageRatingByProduct(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo.");
        }
        Double averageRating = reviewRepository.findAverageRatingByProductId(productId);
        return averageRating != null ? averageRating : 0.0;
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
}
