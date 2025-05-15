package com.trade.icesi_trade.Service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.trade.icesi_trade.Service.Interface.ProductService;
import com.trade.icesi_trade.model.Product;
import com.trade.icesi_trade.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Creates a new product and saves it to the repository.
     *
     * @param product The product to be created. Must not be null and must have a non-empty title.
     * @return The saved product instance.
     * @throws IllegalArgumentException If the product is null or if the product's title is null or empty.
     */
    @Override
    public Product createProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        if (product.getTitle() == null || product.getTitle().isEmpty()) {
            throw new IllegalArgumentException("El producto debe tener un título.");
        }
        
        product.setCreatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    /**
     * Updates an existing product with the provided details.
     *
     * @param id The ID of the product to be updated. Must not be null.
     * @param product The product object containing the updated details. Must not be null.
     * @return The updated product after saving it to the repository.
     * @throws IllegalArgumentException If the provided ID or product is null.
     * @throws NoSuchElementException If no product is found with the given ID.
     */
    @Override
    public Product updateProduct(Long id, Product product) {
        if (id == null || product == null) {
            throw new IllegalArgumentException("El ID del producto y los datos a actualizar no pueden ser nulos.");
        }

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con el ID: " + id));
        
        existingProduct.setTitle(product.getTitle());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setLocation(product.getLocation());
        existingProduct.setStatus(product.getStatus());
        existingProduct.setUpdatedAt(LocalDateTime.now());
        existingProduct.setCategory(product.getCategory());

        return productRepository.save(existingProduct);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to be deleted; must not be null.
     * @return {@code true} if the product was successfully deleted, {@code false} if the product does not exist.
     * @throws IllegalArgumentException if the provided ID is null.
     */
    @Override
    public boolean deleteProduct(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo.");
        }
        if (!productRepository.existsById(id)) {
            return false;
        }
        productRepository.deleteById(id);
        return true;
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the unique identifier of the product to retrieve; must not be null.
     * @return the product associated with the given ID.
     * @throws IllegalArgumentException if the provided ID is null.
     * @throws NoSuchElementException if no product is found with the given ID.
     */
    @Override
    public Product getProductById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo.");
        }
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con el ID: " + id));
    }

    /**
     * Retrieves a list of all products from the repository.
     *
     * @return a list containing all products.
     */
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a paginated list of all products.
     *
     * @param pageable the pagination and sorting information
     * @return a page containing the products
     */
    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}
