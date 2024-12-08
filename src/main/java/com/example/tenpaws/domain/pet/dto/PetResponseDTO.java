package com.example.tenpaws.domain.pet.dto;

import com.example.tenpaws.domain.pet.entity.Pet;
import com.example.tenpaws.domain.pet.species.Species;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PetResponseDTO {
    private final Long petId;
    private final String petName;
    private final Species species;
    private final String size;
    private final String age;
    private final String gender;
    private final String neutering;
    private final String reason;
    private final String preAdoption;
    private final String vaccinated;
    private final String extra;
    private final String personality;
    private final int exerciseLevel;
    private final Long shelterId;
    private final String shelterName;
    private final String shelterAddress;
    private final List<String> imageUrls;
    private final Pet.PetStatus status;

    public static PetResponseDTO fromEntity(Pet pet) {
        return PetResponseDTO.builder()
                .petId(pet.getId())
                .petName(pet.getPetName())
                .species(pet.getSpecies())
                .size(pet.getSize())
                .age(pet.getAge())
                .gender(pet.getGender())
                .neutering(pet.getNeutering())
                .reason(pet.getReason())
                .preAdoption(pet.getPreAdoption())
                .vaccinated(pet.getVaccinated())
                .extra(pet.getExtra())
                .personality(pet.getPersonality())
                .exerciseLevel(pet.getExerciseLevel())
                .shelterId(pet.getShelter().getId())
                .shelterName(pet.getShelter().getShelterName())
                .shelterAddress(pet.getShelter().getAddress())
                .imageUrls(pet.getImageUrls() != null ? pet.getImageUrls() : List.of()) // null 처리
                .status(pet.getStatus())
                .build();
    }
}
