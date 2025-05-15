package com.trade.icesi_trade.Service.Impl;

import com.trade.icesi_trade.Service.Interface.SaleService;
import com.trade.icesi_trade.model.Product;
import com.trade.icesi_trade.model.Sale;
import com.trade.icesi_trade.model.User;
import com.trade.icesi_trade.repository.ProductRepository;
import com.trade.icesi_trade.repository.SaleRepository;
import com.trade.icesi_trade.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SaleServiceImpl implements SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Sale save(Sale sale) {
        if (sale == null || sale.getBuyer() == null || sale.getProduct() == null) {
            throw new IllegalArgumentException("Venta, comprador y producto son obligatorios.");
        }

        User buyer = userRepository.findById(sale.getBuyer().getId())
                .orElseThrow(() -> new NoSuchElementException("Comprador no encontrado."));
        Product product = productRepository.findById(sale.getProduct().getId())
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado."));

        sale.setBuyer(buyer);
        sale.setProduct(product);
        sale.setCreatedAt(LocalDateTime.now());

        return saleRepository.save(sale);
    }

    @Override
    public Sale update(Long id, Sale sale) {
        if (id == null || sale == null) {
            throw new IllegalArgumentException("ID y datos de la venta no pueden ser nulos.");
        }

        Sale existing = saleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Venta no encontrada con ID: " + id));

        User buyer = userRepository.findById(sale.getBuyer().getId())
                .orElseThrow(() -> new NoSuchElementException("Comprador no encontrado."));
        Product product = productRepository.findById(sale.getProduct().getId())
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado."));

        existing.setBuyer(buyer);
        existing.setProduct(product);
        return saleRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!saleRepository.existsById(id)) {
            throw new NoSuchElementException("La venta no existe.");
        }
        saleRepository.deleteById(id);
    }

    @Override
    public Sale findById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Venta no encontrada con ID: " + id));
    }

    @Override
    public List<Sale> findAll() {
        return saleRepository.findAll();
    }
}
