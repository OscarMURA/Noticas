package com.trade.icesi_trade.Service.Interface;

import com.trade.icesi_trade.model.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing products.
 * Provides methods for creating, updating, deleting, and retrieving products.
 */
public interface ProductService {

    Product createProduct(Product product);

    Product updateProduct(Long id, Product product);

    boolean deleteProduct(Long id);

    Product getProductById(Long id);

    List<Product> getAllProducts();

    Page<Product> getAllProducts(Pageable pageable);
}
