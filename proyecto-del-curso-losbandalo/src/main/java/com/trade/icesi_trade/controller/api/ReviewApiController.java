package com.trade.icesi_trade.controller.api;

import com.trade.icesi_trade.Service.Interface.ReviewService;
import com.trade.icesi_trade.Service.Interface.UserService;
import com.trade.icesi_trade.Service.Interface.ProductService;
import com.trade.icesi_trade.dtos.ReviewDto;
import com.trade.icesi_trade.mappers.ReviewMapper;
import com.trade.icesi_trade.model.Review;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "CRUD operations for reviews")
public class ReviewApiController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final ProductService productService;
    private final ReviewMapper reviewMapper;

    @Operation(summary = "Get all reviews")
    @GetMapping
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        List<ReviewDto> dtos = reviewService.getAllReviews().stream()
                .map(reviewMapper::entityToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Get review by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable Long id) {
        List<Review> all = reviewService.getAllReviews();
        Review review = all.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Review not found"));

        return ResponseEntity.ok(reviewMapper.entityToDto(review));
    }

    @Operation(summary = "Get reviews by product ID")
    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto dto) {
        Review review = reviewMapper.dtoToEntity(dto);

        review.setProduct(productService.getProductById(dto.getProductId()));
        review.setReviewer(userService.findUserById(dto.getReviewerId()));
        review.setReviewee(userService.findUserById(dto.getRevieweeId()));
        review.setCreatedAt(LocalDateTime.now());

        Review created = reviewService.createReview(review);
        return ResponseEntity.status(201).body(reviewMapper.entityToDto(created));
    }

    @Operation(summary = "Update review by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Long id, @RequestBody ReviewDto dto) {
        Review review = reviewMapper.dtoToEntity(dto);

        review.setProduct(productService.getProductById(dto.getProductId()));
        review.setReviewer(userService.findUserById(dto.getReviewerId()));
        review.setReviewee(userService.findUserById(dto.getRevieweeId()));
        review.setCreatedAt(LocalDateTime.now());

        Review updated = reviewService.updateReview(id, review);
        return ResponseEntity.ok(reviewMapper.entityToDto(updated));
    }

    @Operation(summary = "Delete review by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
