package com.smp.mail.mapper;

import com.smp.mail.dto.ServiceDTO;
import com.smp.mail.entity.ItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(source = "service.id", target = "id")
    @Mapping(source = "service.name", target = "name")
    @Mapping(source = "service.code", target = "code")
    ServiceDTO itemEntityToServiceDTO(ItemEntity itemEntity);
}