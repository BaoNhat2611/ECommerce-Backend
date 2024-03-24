package com.nhat.repository;

import com.nhat.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query("select r from Rating r where r.product.id = :productId")
    List<Rating> getProductsRating(@Param("productId") Long productId);
}
