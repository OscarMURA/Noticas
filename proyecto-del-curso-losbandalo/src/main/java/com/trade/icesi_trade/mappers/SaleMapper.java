// This code snippet is defining a Java interface named `SaleMapper` that uses MapStruct for mapping
// between `Sale` entities and `SaleDto` data transfer objects (DTOs). Here's a breakdown of what each
// part of the code is doing:
// SaleMapper.java
package com.trade.icesi_trade.mappers;

import com.trade.icesi_trade.dtos.SaleDto;
import com.trade.icesi_trade.model.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    @Mapping(source = "buyer.id", target = "buyerId")
    @Mapping(source = "product.id", target = "productId")
    SaleDto entityToDto(Sale sale);

    @Mapping(target = "buyer.id", source = "buyerId")
    @Mapping(target = "product.id", source = "productId")
    Sale dtoToEntity(SaleDto dto);
}
