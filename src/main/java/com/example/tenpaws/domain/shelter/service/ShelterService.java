package com.example.tenpaws.domain.shelter.service;

import com.example.tenpaws.domain.shelter.dto.ShelterRequestDTO;
import com.example.tenpaws.domain.shelter.dto.ShelterResponseDTO;
import com.example.tenpaws.domain.shelter.entity.Shelter;
import com.example.tenpaws.domain.shelter.repository.ShelterRepository;
import com.example.tenpaws.global.exception.BaseException;
import com.example.tenpaws.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShelterService {
    private final ShelterRepository shelterRepository;

    public List<ShelterResponseDTO> getAllShelters() {
        return shelterRepository.findAll().stream()
                .map(ShelterResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public ShelterResponseDTO getShelterById(Long id) {
        Shelter shelter = shelterRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.SHELTER_NOT_FOUND));
        return ShelterResponseDTO.fromEntity(shelter);

    }

    public ShelterResponseDTO createShelter(ShelterRequestDTO requestDTO) {
        Shelter shelter = requestDTO.toEntity();
        Shelter savedShelter = shelterRepository.save(shelter);
        return ShelterResponseDTO.fromEntity(savedShelter);
    }

    public ShelterResponseDTO updateShelter(Long id, ShelterRequestDTO requestDTO) {
        Shelter shelter = getShelter(id);
        if (!shelterRepository.existsById(id)) {
            throw new BaseException(ErrorCode.SHELTER_NOT_FOUND);
        }
        shelter.updateFields(requestDTO);
        Shelter updatedShelter = shelterRepository.save(shelter);
        return ShelterResponseDTO.fromEntity(updatedShelter);
    }

    public void deleteShelter(Long id) {
        if (!shelterRepository.existsById(id)) {
            throw new BaseException(ErrorCode.SHELTER_NOT_FOUND);
        }
        shelterRepository.deleteById(id);
    }

    private Shelter getShelter(Long id) {
        return shelterRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.SHELTER_NOT_FOUND));
    }// 여기에 두는게 맞나..?
}