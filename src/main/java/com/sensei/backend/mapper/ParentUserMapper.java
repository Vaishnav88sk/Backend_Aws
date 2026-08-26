package com.sensei.backend.mapper;

import com.sensei.backend.dto.ParentUserDTO;
import com.sensei.backend.entity.ParentUser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ParentUserMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ParentUserDTO toDto(ParentUser entity) {
        return modelMapper.map(entity, ParentUserDTO.class);
    }

    public ParentUser toEntity(ParentUserDTO dto) {
        return modelMapper.map(dto, ParentUser.class);
    }

    public void updateEntityFromDto(ParentUserDTO dto, ParentUser entity) {
        modelMapper.map(dto, entity);
    }
}
