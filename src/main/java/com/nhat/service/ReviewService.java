package com.nhat.service;

import com.nhat.exception.ProductException;
import com.nhat.model.Review;
import com.nhat.model.User;
import com.nhat.request.ReviewRequest;

import java.util.List;

public interface ReviewService {
    Review createReview(ReviewRequest request, User user) throws ProductException;
    List<Review> getAllReviews(Long productId);
}
