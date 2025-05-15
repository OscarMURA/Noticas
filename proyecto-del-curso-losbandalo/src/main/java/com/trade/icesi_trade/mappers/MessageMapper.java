// This code snippet is defining a Java interface named `MessageMapper` that uses MapStruct for mapping
// between `Message` entities and `MessageDto` data transfer objects (DTOs). MapStruct is a Java
// annotation processor that simplifies the implementation of mappings between Java bean types.
package com.trade.icesi_trade.mappers;

import com.trade.icesi_trade.dtos.MessageDto;
import com.trade.icesi_trade.model.Message;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    MessageDto entityToDto(Message message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Message dtoToEntity(MessageDto dto);
}