// This Java code snippet defines a mapper interface named `CategoryMapper` using MapStruct library.
// The purpose of this mapper is to convert objects between the `Category` entity class and the
// `CategoryDto` data transfer object class.
package com.trade.icesi_trade.mappers;

import com.trade.icesi_trade.dtos.CategoryDto;
import com.trade.icesi_trade.model.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto entityToDto(Category category);
    Category dtoToEntity(CategoryDto dto);
}
