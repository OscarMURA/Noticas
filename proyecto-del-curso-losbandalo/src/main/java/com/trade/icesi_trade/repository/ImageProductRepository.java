package com.trade.icesi_trade.repository;

import com.trade.icesi_trade.model.ImageProduct;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageProductRepository extends JpaRepository<ImageProduct, Long> {
            ImageProduct findByProduct_Id(Long product_id);
            Optional<ImageProduct> findById(Long imageId);
}