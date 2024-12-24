package com.smp.mail.mapper;

import com.smp.mail.dto.ServiceDTO;
import com.smp.mail.entity.ServiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "name", target = "name")
    ServiceDTO toDto(ServiceEntity serviceEntity);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "name", target = "name")
    ServiceEntity toEntity(ServiceDTO serviceDTO);

    List<ServiceDTO> toDtoList(List<ServiceEntity> serviceEntities);
}