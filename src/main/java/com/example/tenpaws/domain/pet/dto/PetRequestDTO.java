package com.example.tenpaws.domain.pet.dto;

import com.example.tenpaws.domain.pet.entity.Image;
import com.example.tenpaws.domain.pet.entity.Pet;
import com.example.tenpaws.domain.shelter.entity.Shelter;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private List<String> imageUrls;

    public Pet toEntity(Shelter shelter) {
        Set<Image> images = new HashSet<>();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                Image image = new Image();
                image.setImageUrl(imageUrl);
                images.add(image);
            }
        }

        return Pet.builder()
                .species(this.species)
                .size(this.size)
                .age(this.age)
                .personality(this.personality)
                .exerciseLevel(this.exerciseLevel)
                .shelter(shelter)
                .images(images) // 이미지 추가
                .build();
    }
}