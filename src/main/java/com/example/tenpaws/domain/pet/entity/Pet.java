package com.example.tenpaws.domain.pet.entity;

import com.example.tenpaws.domain.pet.dto.PetRequestDTO;
import com.example.tenpaws.domain.pet.species.Species;
import com.example.tenpaws.domain.shelter.entity.Shelter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    @Column(name = "pet_name")
    private String petName;

    @Enumerated(EnumType.STRING)
    @Column(name = "species", nullable = false, length = 50)
    private Species species;

    @Column(name = "size", nullable = false, length = 50)
    private String size;

    @Column(name = "age", nullable = false)
    private String age;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "neutering", nullable = false)
    private String neutering; // 중성화

    @Column(name = "reason", nullable = false)
    private String reason; // 보호소 입소 이유

    @Column(name = "pre_adoption", nullable = false)
    private String preAdoption; // 이전 가정환경

    @Column(name = "vaccinated")
    private String vaccinated;

    @Column(name = "extra")
    private String extra;

    @Column(name = "personality", nullable = false)
    private String personality;

    @Column(name = "exercise_level", nullable = false)
    private int exerciseLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id")
    private Shelter shelter;

    @ElementCollection
    @CollectionTable(name = "pet_images", joinColumns = @JoinColumn(name = "pet_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;


    // pet 신청 상태
    @Enumerated(EnumType.STRING)
    private PetStatus status = PetStatus.AVAILABLE; // 기본값: 신청 가능
    public enum PetStatus {
        AVAILABLE, // 신청 가능
        APPLIED    // 신청 완료
    }

    @Builder
    public Pet(Long id, String petName, Species species,  String size, String age, String gender, String neutering, String reason,
               String preAdoption, String vaccinated, String extra, String personality, int exerciseLevel, Shelter shelter, List<String> imageUrls, PetStatus status) {
        this.id = id;
        this.petName = petName;
        this.species = species;
        this.size = size;
        this.age = age;
        this.gender = gender;
        this.neutering = neutering;
        this.reason = reason;
        this.preAdoption = preAdoption;
        this.vaccinated = vaccinated;
        this.extra = extra;
        this.personality = personality;
        this.exerciseLevel = exerciseLevel;
        this.shelter = shelter;
        this.imageUrls = imageUrls;
        this.status = status;
    }

    // 필드 업데이트 메서드
    public void updateFields(PetRequestDTO requestDTO) {
        this.petName = requestDTO.getPetName();
        this.species = requestDTO.getSpecies();
        this.size = requestDTO.getSize();
        this.age = requestDTO.getAge();
        this.gender = requestDTO.getGender();
        this.neutering = requestDTO.getNeutering();
        this.reason = requestDTO.getReason();
        this.preAdoption = requestDTO.getPreAdoption();
        this.vaccinated = requestDTO.getVaccinated();
        this.extra = requestDTO.getExtra();
        this.personality = requestDTO.getPersonality();
        this.exerciseLevel = requestDTO.getExerciseLevel();
    }
}
