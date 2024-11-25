package com.example.tenpaws.domain.shelter.dto;

import com.example.tenpaws.domain.shelter.entity.Shelter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShelterRequestDTO {
    @NotBlank(message = "이름은 필수 항목입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    private String pw;

    @NotBlank(message = "보호소 이름은 필수 항목입니다.")
    private String shelterName;

    @NotBlank(message = "주소는 필수 항목입니다.")
    private String address;

    private String phoneNumber;

    @Email(message = "유효한 이메일을 입력해주세요.")
    private String email;

    public Shelter toEntity() {
        return Shelter.builder()
                .username(this.username)
                .pw(this.pw)
                .shelterName(this.shelterName)
                .address(this.address)
                .phoneNumber(this.phoneNumber)
                .email(this.email)
                .build();
    }
}
