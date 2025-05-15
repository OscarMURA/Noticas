package com.trade.icesi_trade.Service.Impl;

import com.trade.icesi_trade.Service.Interface.ImageProductService;
import com.trade.icesi_trade.model.ImageProduct;
import com.trade.icesi_trade.model.Product;
import com.trade.icesi_trade.repository.ImageProductRepository;
import com.trade.icesi_trade.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ImageProductServiceImpl implements ImageProductService {

    @Value("${image.base.url}")
    private String imageBaseUrl;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; 

    @Autowired
    private ImageProductRepository imageProductRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Uploads an image file and associates it with a product.
     *
     * @param file      The image file to be uploaded. Must not be null or empty.
     * @param productId The ID of the product to associate the image with. Must not be null.
     * @return The saved ImageProduct entity containing the image URL and associated product.
     * @throws IllegalArgumentException If the file is null, empty, exceeds the maximum allowed size,
     *                                  has an invalid extension, or the productId is null.
     * @throws NoSuchElementException   If no product is found with the given productId.
     */
    @Override
    public ImageProduct uploadImage(MultipartFile file, Long productId) {
        if(file == null || file.isEmpty()){
            throw new IllegalArgumentException("El archivo no puede ser nulo o estar vacío");
        }
        if(productId == null){
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }
        
        if(file.getSize() > MAX_FILE_SIZE){
            throw new IllegalArgumentException("El tamaño del archivo excede el límite permitido de 5MB");
        }
        
        String originalFilename = file.getOriginalFilename();
        if(originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("El archivo debe tener una extensión válida");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if(!ALLOWED_EXTENSIONS.contains(extension)){
            throw new IllegalArgumentException("Formato de imagen no permitido. Solo se aceptan: " + ALLOWED_EXTENSIONS);
        }

        String uniqueFileName = UUID.randomUUID().toString() + "." + extension;
        String imageUrl = imageBaseUrl + uniqueFileName;
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con ID: " + productId));
        
        // Crear la entidad ImageProduct y asociarla al producto
        ImageProduct imageProduct = ImageProduct.builder()
                .id(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                .url(imageUrl)
                .product(product)
                .build();
        
        return imageProductRepository.save(imageProduct);
    }

    /**
     * Retrieves the image associated with a specific product ID.
     *
     * @param productId the ID of the product whose image is to be retrieved.
     *                  Must not be null.
     * @return the ImageProduct associated with the given product ID.
     * @throws IllegalArgumentException if the provided productId is null.
     * @throws NoSuchElementException if no image is found for the given product ID.
     */
    @Override
    public ImageProduct getImageByProductId(Long productId) {
        if(productId == null){
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }
        ImageProduct imageProduct = imageProductRepository.findByProduct_Id(productId);
        if(imageProduct == null){
            throw new NoSuchElementException("No se encontró imagen para el producto con ID: " + productId);
        }
        return imageProduct;
    }

    /**
     * Deletes an image by its ID.
     *
     * @param imageId the ID of the image to be deleted; must not be null.
     * @return {@code true} if the image was successfully deleted, {@code false} if the image does not exist.
     * @throws IllegalArgumentException if the provided imageId is null.
     */
    @Override
    public boolean deleteImage(Long imageId) {
        if(imageId == null){
            throw new IllegalArgumentException("El ID de la imagen no puede ser nulo");
        }
        if(!imageProductRepository.existsById(imageId)){
            return false;
        }
        imageProductRepository.deleteById(imageId);
        return true;
    }
}
