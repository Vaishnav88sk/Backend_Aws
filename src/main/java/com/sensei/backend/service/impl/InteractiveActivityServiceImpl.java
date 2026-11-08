package com.sensei.backend.service.impl;

import com.sensei.backend.dto.interactiveactivity.InteractiveActivityRequestDTO;
import com.sensei.backend.dto.interactiveactivity.InteractiveActivityResponseDTO;
import com.sensei.backend.entity.InteractiveActivity;
import com.sensei.backend.entity.SubModule;
import com.sensei.backend.repository.InteractiveActivityRepository;
import com.sensei.backend.repository.SubModuleRepository;
import com.sensei.backend.service.InteractiveActivityService;
import com.sensei.backend.service.MediaStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InteractiveActivityServiceImpl implements InteractiveActivityService {

    private final InteractiveActivityRepository repository;
    private final SubModuleRepository subModuleRepository;
    private final com.sensei.backend.repository.ChildInteractiveActivityProgressRepository activityProgressRepo;
    private final MediaStorageService mediaStorageService;

    private InteractiveActivityResponseDTO map(InteractiveActivity a) {
        InteractiveActivityResponseDTO dto = new InteractiveActivityResponseDTO();
        dto.setId(a.getId());
        dto.setSubModuleId(a.getSubModule().getId());
        dto.setTitle(a.getTitle());
        dto.setLearningOutcome(a.getLearningOutcome());
        dto.setKeyObjectives(a.getKeyObjectives());
        dto.setReferenceVideo(mediaStorageService.toPublicUrl(a.getReferenceVideo()));
        dto.setCoverImage(mediaStorageService.toPublicUrl(a.getCoverImage()));
        dto.setObjective(a.getObjective());
        dto.setActivityType(a.getActivityType());
        dto.setOrderIndex(a.getOrderIndex());
        dto.setIsActive(a.getIsActive());
        return dto;
    }

    @Override
    @Transactional
    public InteractiveActivityResponseDTO create(InteractiveActivityRequestDTO dto) {

        SubModule subModule = subModuleRepository.findById(dto.getSubModuleId())
                .orElseThrow(() -> new RuntimeException("SubModule not found"));

        InteractiveActivity activity = InteractiveActivity.builder()
                .subModule(subModule)
                .title(dto.getTitle())
                .learningOutcome(dto.getLearningOutcome())
                .keyObjectives(dto.getKeyObjectives())
                .referenceVideo(mediaStorageService.toStorageKey(dto.getReferenceVideo()))
                .coverImage(mediaStorageService.toStorageKey(dto.getCoverImage()))
                .objective(dto.getObjective())
                .activityType(dto.getActivityType())
                .orderIndex(dto.getOrderIndex())
                .isActive(true)
                .build();

        return map(repository.save(activity));
    }

    @Override
    public List<InteractiveActivityResponseDTO> getBySubModule(UUID subModuleId, UUID childId) {
        return repository.findBySubModule_IdAndIsActiveTrueOrderByOrderIndexAsc(subModuleId)
                .stream()
                .map(a -> {
                    InteractiveActivityResponseDTO dto = map(a);
                    if (childId != null) {
                        activityProgressRepo.findByChildIdAndInteractiveActivity(childId, a)
                                .ifPresent(p -> dto.setStatus(p.getStatus()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public InteractiveActivityResponseDTO getById(UUID id) {
        return map(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found")));
    }

    @Override
    @Transactional
    public InteractiveActivityResponseDTO update(UUID id, InteractiveActivityRequestDTO dto) {
        InteractiveActivity a = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        a.setTitle(dto.getTitle());
        a.setLearningOutcome(dto.getLearningOutcome());
        a.setKeyObjectives(dto.getKeyObjectives());

        String newRefVideo = mediaStorageService.toStorageKey(dto.getReferenceVideo());
        if (StringUtils.hasText(a.getReferenceVideo()) && !a.getReferenceVideo().equals(newRefVideo)) {
            mediaStorageService.deleteFile(a.getReferenceVideo());
        }
        a.setReferenceVideo(newRefVideo);

        String newCover = mediaStorageService.toStorageKey(dto.getCoverImage());
        if (StringUtils.hasText(a.getCoverImage()) && !a.getCoverImage().equals(newCover)) {
            mediaStorageService.deleteFile(a.getCoverImage());
        }
        a.setCoverImage(newCover);

        a.setObjective(dto.getObjective());
        a.setActivityType(dto.getActivityType());
        a.setOrderIndex(dto.getOrderIndex());

        return map(repository.save(a));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        InteractiveActivity a = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        a.setIsActive(false);
        repository.save(a);
    }

    @Override
public UUID getSubjectIdBySubModule(UUID subModuleId) {
    return subModuleRepository.findById(subModuleId)
            .orElseThrow(() -> new RuntimeException("SubModule not found"))
            .getModule()
            .getSubject()
            .getId();
}

}
