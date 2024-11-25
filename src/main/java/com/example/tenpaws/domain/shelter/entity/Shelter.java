package com.example.tenpaws.domain.shelter.entity;

import com.example.tenpaws.domain.pet.entity.Pet;
import com.example.tenpaws.domain.shelter.dto.ShelterRequestDTO;
import com.example.tenpaws.global.entity.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "shelters")
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shelter_id")
    private Long id;

    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String pw;

    @Column(name = "shelter_name", nullable = false, length = 255)
    private String shelterName;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @OneToMany(mappedBy = "shelter", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pet> pets = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;

    public void addPet(Pet pet) {
        if (!pets.contains(pet)) {
            pets.add(pet);
            pet.setShelter(this);
        }
    }

    // Update method
    public void updateFields(ShelterRequestDTO requestDTO) {
        if (requestDTO.getUsername() != null) this.username = requestDTO.getUsername();
        if (requestDTO.getPw() != null) this.pw = requestDTO.getPw();
        if (requestDTO.getShelterName() != null) this.shelterName = requestDTO.getShelterName();
        if (requestDTO.getAddress() != null) this.address = requestDTO.getAddress();
        if (requestDTO.getPhoneNumber() != null) this.phoneNumber = requestDTO.getPhoneNumber();
        if (requestDTO.getEmail() != null) this.email = requestDTO.getEmail();
    }

    public void removePet(Pet pet) {
        if (pets.contains(pet)) {
            pets.remove(pet);
            pet.setShelter(null);
        }
    }

    @Builder
    public Shelter(Long id, String username, String pw, String shelterName, String address, String phoneNumber, String email) {
        this.id = id;
        this.username = username;
        this.pw = pw;
        this.shelterName = shelterName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}

