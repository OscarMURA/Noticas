// SaleApiController.java
package com.trade.icesi_trade.controller.api;

import com.trade.icesi_trade.Service.Interface.SaleService;
import com.trade.icesi_trade.dtos.SaleDto;
import com.trade.icesi_trade.mappers.SaleMapper;
import com.trade.icesi_trade.model.Sale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin
@Tag(name = "Sales", description = "CRUD operations for sales")
public class SaleApiController {

    @Autowired
    private SaleService saleService;

    @Autowired
    private SaleMapper saleMapper;

    @Operation(summary = "Get all sales")
    @GetMapping
    public ResponseEntity<List<SaleDto>> getAll() {
        List<SaleDto> sales = saleService.findAll().stream()
                .map(saleMapper::entityToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sales);
    }

    @Operation(summary = "Get sale by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SaleDto> getById(@PathVariable Long id) {
        Sale sale = saleService.findById(id);
        return ResponseEntity.ok(saleMapper.entityToDto(sale));
    }

    @Operation(summary = "Create a new sale")
    @PostMapping
    public ResponseEntity<SaleDto> create(@RequestBody SaleDto dto) {
        Sale saved = saleService.save(saleMapper.dtoToEntity(dto));
        return ResponseEntity.status(201).body(saleMapper.entityToDto(saved));
    }

    @Operation(summary = "Update an existing sale")
    @PutMapping("/{id}")
    public ResponseEntity<SaleDto> update(@PathVariable Long id, @RequestBody SaleDto dto) {
        Sale updated = saleService.update(id, saleMapper.dtoToEntity(dto));
        return ResponseEntity.ok(saleMapper.entityToDto(updated));
    }

    @Operation(summary = "Delete a sale by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        saleService.delete(id);
        return ResponseEntity.ok("Venta eliminada.");
    }

    @Operation(summary = "Get sales by seller ID")
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<SaleDto>> getSalesByBuyer(@PathVariable Long buyerId) {
        List<Sale> sales = saleService.findAll().stream()
                .filter(sale -> sale.getBuyer() != null && sale.getBuyer().getId().equals(buyerId))
                .toList();
        List<SaleDto> saleDtos = sales.stream().map(saleMapper::entityToDto).toList();
        return ResponseEntity.ok(saleDtos);
    }
}
