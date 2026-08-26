package com.sensei.backend.service;

import com.sensei.backend.dto.ParentUserDTO;
import com.sensei.backend.entity.ParentUser;
import com.sensei.backend.exception.ResourceNotFoundException;
import com.sensei.backend.repository.ParentUserRepository;
import com.sensei.backend.repository.PricingPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author vaishnav88sk
 */
@ExtendWith(MockitoExtension.class)
class ParentUserServiceTest {

    @Mock
    private ParentUserRepository parentUserRepository;

    @Mock
    private PricingPlanRepository pricingPlanRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ParentUserService parentUserService;

    private ParentUser parentUser;
    private ParentUserDTO parentUserDTO;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        parentUser = new ParentUser();
        parentUser.setParentId(testId);
        parentUser.setEmail("test@example.com");

        parentUserDTO = new ParentUserDTO();
        parentUserDTO.setParentId(testId);
        parentUserDTO.setEmail("test@example.com");
    }

    @Test
    void testCreateParentUser() {
        when(modelMapper.map(parentUserDTO, ParentUser.class)).thenReturn(parentUser);
        when(parentUserRepository.save(any(ParentUser.class))).thenReturn(parentUser);
        when(modelMapper.map(parentUser, ParentUserDTO.class)).thenReturn(parentUserDTO);

        ParentUserDTO result = parentUserService.createParentUser(parentUserDTO);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(parentUserRepository, times(1)).save(parentUser);
    }

    @Test
    void testGetParentUserByIdSuccess() {
        when(parentUserRepository.findById(testId)).thenReturn(Optional.of(parentUser));
        when(modelMapper.map(parentUser, ParentUserDTO.class)).thenReturn(parentUserDTO);

        ParentUserDTO result = parentUserService.getParentUserById(testId);

        assertNotNull(result);
        assertEquals(testId, result.getParentId());
    }

    @Test
    void testGetParentUserByIdThrowsNotFound() {
        when(parentUserRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            parentUserService.getParentUserById(testId);
        });
    }

    @Test
    void testFindByEmail() {
        when(parentUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(parentUser));

        Optional<ParentUser> result = parentUserService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }
}
