package com.trade.icesi_trade.repository;

import com.trade.icesi_trade.model.FavoriteProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {
            
            FavoriteProduct findByProduct_IdAndUser_Id(Long product_id, Long user_id);
            FavoriteProduct findByProduct_Id(Long product_id);
            FavoriteProduct findByUser_Id(Long user_id);
}