package com.example.tenpaws.domain.pet.dto;

import com.example.tenpaws.domain.pet.entity.Pet;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PetRequestDTO {
    private String species;
    private String size;
    private int age;
    private String personality;
    private int exerciseLevel;

    public Pet toEntity() {
        return Pet.builder()
                .species(this.species)
                .size(this.size)
                .age(this.age)
                .personality(this.personality)
                .exerciseLevel(this.exerciseLevel)
                .build();
    }



}

