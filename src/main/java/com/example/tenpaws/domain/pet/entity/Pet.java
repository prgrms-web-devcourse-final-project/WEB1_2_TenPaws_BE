package com.example.tenpaws.domain.pet.entity;

import com.example.tenpaws.domain.pet.dto.PetRequestDTO;
import com.example.tenpaws.domain.shelter.entity.Shelter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "species", nullable = false, length = 255)
    private String species;

    @Column(name = "size", nullable = false, length = 50)
    private String size;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "personality", nullable = false, length = 255)
    private String personality;

    @Column(name = "exercise_level", nullable = false)
    private int exerciseLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id")
    private Shelter shelter;


    @Builder
    public Pet(Long id, String species, String size, int age, String personality, int exerciseLevel, Shelter shelter) {
        this.id = id;
        this.species = species;
        this.size = size;
        this.age = age;
        this.personality = personality;
        this.exerciseLevel = exerciseLevel;
        this.shelter = shelter;
    }

    // 필드 업데이트 메서드
    public void updateFields(PetRequestDTO requestDTO) {
        if (requestDTO.getSpecies() != null) this.species = requestDTO.getSpecies();
        if (requestDTO.getSize() != null) this.size = requestDTO.getSize();
        if (requestDTO.getAge() != 0) this.age = requestDTO.getAge();
        if (requestDTO.getPersonality() != null) this.personality = requestDTO.getPersonality();
        if (requestDTO.getExerciseLevel() != 0) this.exerciseLevel = requestDTO.getExerciseLevel();
    }


}