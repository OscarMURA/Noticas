package com.trade.icesi_trade.Service.Interface;

import com.trade.icesi_trade.model.Review;
import java.util.List;

public interface ReviewService {

    Review createReview(Review review);

    Review updateReview(Long reviewId, Review review);

    void deleteReview(Long reviewId);

    List<Review> getAllReviews();

    List<Review> getReviewsByProduct(Long productId);

    List<Review> getReviewsByReviewer(Long reviewerId);

    List<Review> getReviewsByReviewee(Long revieweeId);

    Double calculateAverageRatingByProduct(Long productId);
}
