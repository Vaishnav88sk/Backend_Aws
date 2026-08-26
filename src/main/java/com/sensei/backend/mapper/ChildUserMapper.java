package com.sensei.backend.mapper;

import com.sensei.backend.dto.ChildUserDTO;
import com.sensei.backend.entity.ChildUser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChildUserMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ChildUserDTO toDto(ChildUser entity) {
        return modelMapper.map(entity, ChildUserDTO.class);
    }

    public ChildUser toEntity(ChildUserDTO dto) {
        return modelMapper.map(dto, ChildUser.class);
    }

    public void updateEntityFromDto(ChildUserDTO dto, ChildUser entity) {
        modelMapper.map(dto, entity);
    }
}
