// This Java code snippet defines a MapStruct mapper interface named `FavoriteProductMapper`. MapStruct
// is a code generator that simplifies the implementation of mappings between Java bean types. In this
// interface:
package com.trade.icesi_trade.mappers;

import com.trade.icesi_trade.dtos.FavoriteProductDto;
import com.trade.icesi_trade.model.FavoriteProduct;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FavoriteProductMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "product.id", target = "productId")
    FavoriteProductDto entityToDto(FavoriteProduct favorite);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "product.id", source = "productId")
    FavoriteProduct dtoToEntity(FavoriteProductDto dto);
}
