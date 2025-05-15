package com.trade.icesi_trade.controller.api;

import com.trade.icesi_trade.Service.Interface.ProductService;
import com.trade.icesi_trade.dtos.ProductDto;
import com.trade.icesi_trade.mappers.ProductMapper;
import com.trade.icesi_trade.model.Product;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
@Tag(name = "Products", description = "CRUD operations for products")
public class ProductApiController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<List<ProductDto>> getAll() {
        List<ProductDto> products = productService.getAllProducts()
                .stream()
                .map(productMapper::entityToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(productMapper.entityToDto(product));
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto dto) {
        Product created = productService.createProduct(productMapper.dtoToEntity(dto));
        return new ResponseEntity<>(productMapper.entityToDto(created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<ProductDto> update(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
        Product updated = productService.updateProduct(id, productMapper.dtoToEntity(dto));
        return ResponseEntity.ok(productMapper.entityToDto(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product by ID")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully.");
    }
}
