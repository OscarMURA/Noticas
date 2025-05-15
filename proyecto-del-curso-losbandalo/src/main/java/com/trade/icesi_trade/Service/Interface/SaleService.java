package com.trade.icesi_trade.Service.Interface;

import com.trade.icesi_trade.model.Sale;
import java.util.List;

public interface SaleService {
    Sale save(Sale sale);
    Sale update(Long id, Sale sale);
    void delete(Long id);
    Sale findById(Long id);
    List<Sale> findAll();
}
