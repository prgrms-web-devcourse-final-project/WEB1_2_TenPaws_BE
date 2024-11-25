package com.example.tenpaws.domain.pet.dto;

import com.example.tenpaws.domain.pet.entity.Pet;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PetResponseDTO {
    private Long petId;
    private String species;
    private String size;
    private int age;
    private String personality;
    private int exerciseLevel;

    public static PetResponseDTO fromEntity(Pet pet) {
        PetResponseDTO dto = new PetResponseDTO();
        dto.setPetId(pet.getId());
        dto.setSpecies(pet.getSpecies());
        dto.setSize(pet.getSize());
        dto.setAge(pet.getAge());
        dto.setPersonality(pet.getPersonality());
        dto.setExerciseLevel(pet.getExerciseLevel());
        return dto;
    }
}
