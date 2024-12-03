package com.example.tenpaws.domain.user.service;

import com.example.tenpaws.domain.shelter.dto.ShelterRequestDTO;
import com.example.tenpaws.domain.shelter.entity.Shelter;
import com.example.tenpaws.domain.shelter.repository.ShelterRepository;
import com.example.tenpaws.domain.user.dto.UserJoinDTO;
import com.example.tenpaws.domain.user.entity.User;
import com.example.tenpaws.domain.user.repositoty.UserRepository;
import com.example.tenpaws.global.entity.UserRole;
import com.example.tenpaws.global.exception.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShelterRepository shelterRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserJoinDTO userJoinDTO;
    private ShelterRequestDTO shelterRequestDTO;

    @BeforeEach
    void setUp() {
        userJoinDTO = new UserJoinDTO(
                "user@example.com",
                "johndoe",
                "password123",
                LocalDate.of(1990, 1, 1),
                "010-1234-5678",
                "123 Street",
                "medium",
                "Friendly",
                5,
                UserRole.ROLE_USER
        );

        shelterRequestDTO = new ShelterRequestDTO(
                "password123",
                "Happy shelter",
                "main street 123",
                "02-123-456",
                "example@happyshelter.com",
                UserRole.ROLE_SHELTER
        );
    }


    @Test
    void 적절한_일반유저_회원가입_절차() {
        when(userRepository.existsByEmail(userJoinDTO.getEmail())).thenReturn(false);

        // Given: BCryptPasswordEncoder가 비밀번호를 암호화하도록 설정
        when(bCryptPasswordEncoder.encode(userJoinDTO.getPassword())).thenReturn("encodedPassword");

        User user = userJoinDTO.toEntity();
        user.changePassword("encodedPassword");

        // When: registerUser 메소드 호출
        userService.registerUser(userJoinDTO);

        // Then: UserRepository의 save 메소드가 한 번 호출되어야 한다.
        verify(userRepository, times(1)).save(argThat(savedUser ->
                savedUser.getEmail().equals(user.getEmail()) &&
                        savedUser.getPassword().equals("encodedPassword") &&
                        savedUser.getPhoneNumber().equals(user.getPhoneNumber()) &&
                        savedUser.getAddress().equals(user.getAddress()) &&
                        savedUser.getUserRole().equals(user.getUserRole())
        ));
    }

    @Test
    void 이미_존재하는_일반유저일_때_예외발생() {
        // Given: 이미 존재하는 사용자 이름이 있을 경우를 시뮬레이션
        when(userRepository.existsByEmail(userJoinDTO.getEmail())).thenReturn(true);

        // When & Then: IllegalArgumentException이 발생해야 한다.
        BaseException exception = assertThrows(BaseException.class, () -> {
            userService.registerUser(userJoinDTO);
        });

        // Then: 예외 메시지가 정확한지 확인
        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void 적절한_보호소유저_회원가입_절차() {
        when(shelterRepository.existsByEmail(shelterRequestDTO.getEmail())).thenReturn(false);

        // Given: BCryptPasswordEncoder가 비밀번호를 암호화하도록 설정
        when(bCryptPasswordEncoder.encode(shelterRequestDTO.getPassword())).thenReturn("encodedPassword");

        Shelter shelter = shelterRequestDTO.toEntity();
        shelter.changePassword("encodedPassword");

        // When: registerUser 메소드 호출
        userService.registerShelter(shelterRequestDTO);

        // Then: UserRepository의 save 메소드가 한 번 호출되어야 한다.
        verify(shelterRepository, times(1)).save(argThat(savedUser ->
                savedUser.getEmail().equals(shelter.getEmail()) &&
                        savedUser.getPassword().equals("encodedPassword") &&
                        savedUser.getPhoneNumber().equals(shelter.getPhoneNumber()) &&
                        savedUser.getAddress().equals(shelter.getAddress()) &&
                        savedUser.getUserRole().equals(shelter.getUserRole())
        ));
    }

    @Test
    void 이미_존재하는_보호소유저일_때_예외발생() {
        // Given: 이미 존재하는 사용자 이름이 있을 경우를 시뮬레이션
        when(shelterRepository.existsByEmail(shelterRequestDTO.getEmail())).thenReturn(true);

        // When & Then: IllegalArgumentException이 발생해야 한다.
        BaseException exception = assertThrows(BaseException.class, () -> {
            userService.registerShelter(shelterRequestDTO);
        });

        // Then: 예외 메시지가 정확한지 확인
        assertEquals("User already exists", exception.getMessage());
    }
}
