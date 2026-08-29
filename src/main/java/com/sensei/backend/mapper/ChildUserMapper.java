package com.sensei.backend.mapper;

import com.sensei.backend.dto.ChildUserDTO;
import com.sensei.backend.entity.ChildUser;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ChildUserMapper {
    ChildUserDTO toDto(ChildUser entity);
    ChildUser toEntity(ChildUserDTO dto);
    void updateEntityFromDto(ChildUserDTO dto, @MappingTarget ChildUser entity);
}
