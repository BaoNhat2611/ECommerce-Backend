package com.nhat.service;

import com.nhat.exception.ProductException;
import com.nhat.model.Rating;
import com.nhat.model.User;
import com.nhat.request.RatingRequest;

import java.util.List;

public interface RatingService {
    Rating createRating(RatingRequest request, User user) throws ProductException;
    List<Rating> getProductsRating(Long productId);
}
