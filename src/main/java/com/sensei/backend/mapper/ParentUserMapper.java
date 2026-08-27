package com.sensei.backend.mapper;

import com.sensei.backend.dto.ParentUserDTO;
import com.sensei.backend.entity.ParentUser;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ParentUserMapper {
    ParentUserDTO toDto(ParentUser entity);
    ParentUser toEntity(ParentUserDTO dto);
    void updateEntityFromDto(ParentUserDTO dto, @MappingTarget ParentUser entity);
}
