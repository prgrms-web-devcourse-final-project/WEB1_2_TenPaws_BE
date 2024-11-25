package com.example.tenpaws.domain.shelter.dto;

import com.example.tenpaws.domain.shelter.entity.Shelter;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShelterResponseDTO {
    private Long id;
    private String username;
    private String shelterName;
    private String address;
    private String phoneNumber;
    private String email;

    public static ShelterResponseDTO fromEntity(Shelter shelter) {
        return new ShelterResponseDTO(
                shelter.getId(),
                shelter.getUsername(),
                shelter.getShelterName(),
                shelter.getAddress(),
                shelter.getPhoneNumber(),
                shelter.getEmail()
        );
    }
}