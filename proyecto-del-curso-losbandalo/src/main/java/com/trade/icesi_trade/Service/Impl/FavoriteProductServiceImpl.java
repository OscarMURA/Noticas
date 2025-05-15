package com.trade.icesi_trade.Service.Impl;

import com.trade.icesi_trade.Service.Interface.FavoriteProductService;
import com.trade.icesi_trade.model.FavoriteProduct;
import com.trade.icesi_trade.model.Product;
import com.trade.icesi_trade.model.User;
import com.trade.icesi_trade.repository.FavoriteProductRepository;
import com.trade.icesi_trade.repository.ProductRepository;
import com.trade.icesi_trade.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoriteProductServiceImpl implements FavoriteProductService {

    @Autowired
    private FavoriteProductRepository favoriteProductRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public FavoriteProduct addFavoriteProduct(Long userId, Long productId) {
        if(userId == null || productId == null) {
            throw new IllegalArgumentException("El ID del usuario y del producto no pueden ser nulos.");
        }
        FavoriteProduct existingFavorite = favoriteProductRepository.findByProduct_IdAndUser_Id(productId, userId);
        if(existingFavorite != null) {
            throw new IllegalArgumentException("El producto ya está marcado como favorito para este usuario.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con ID: " + userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con ID: " + productId));

        FavoriteProduct favorite = FavoriteProduct.builder()
                .id(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();

        return favoriteProductRepository.save(favorite);
    }

    @Override
    public boolean removeFavoriteProduct(Long userId, Long productId) {
        if(userId == null || productId == null) {
            throw new IllegalArgumentException("El ID del usuario y del producto no pueden ser nulos.");
        }
        FavoriteProduct favorite = favoriteProductRepository.findByProduct_IdAndUser_Id(productId, userId);
        if(favorite != null) {
            favoriteProductRepository.delete(favorite);
            return true;
        }
        return false;
    }

    @Override
    public FavoriteProduct getFavoriteProduct(Long userId, Long productId) {
        if(userId == null || productId == null) {
            throw new IllegalArgumentException("El ID del usuario y del producto no pueden ser nulos.");
        }
        FavoriteProduct favorite = favoriteProductRepository.findByProduct_IdAndUser_Id(productId, userId);
        if(favorite == null) {
            throw new NoSuchElementException("No se encontró producto favorito para el usuario " +
                                               userId + " y el producto " + productId);
        }
        return favorite;
    }

    @Override
    public List<FavoriteProduct> getFavoriteProductsByUser(Long userId) {
        if(userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo.");
        }
        return favoriteProductRepository.findAll()
                .stream()
                .filter(fp -> fp.getUser() != null && fp.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }
}
