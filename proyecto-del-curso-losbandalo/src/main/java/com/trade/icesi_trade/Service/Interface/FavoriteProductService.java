package com.trade.icesi_trade.Service.Interface;

import com.trade.icesi_trade.model.FavoriteProduct;
import java.util.List;

public interface FavoriteProductService {
    FavoriteProduct addFavoriteProduct(Long userId, Long productId);

    boolean removeFavoriteProduct(Long userId, Long productId);

    FavoriteProduct getFavoriteProduct(Long userId, Long productId);

    List<FavoriteProduct> getFavoriteProductsByUser(Long userId);
}
