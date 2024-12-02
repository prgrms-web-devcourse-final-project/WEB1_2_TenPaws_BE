package com.example.tenpaws.domain.recommendation.service;

import com.example.tenpaws.domain.pet.dto.PetResponseDTO;
import com.example.tenpaws.domain.pet.entity.Pet;
import com.example.tenpaws.domain.pet.repository.PetRepository;
import com.example.tenpaws.domain.user.entity.User;
import com.example.tenpaws.global.exception.BaseException;
import com.example.tenpaws.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RecommendService {

    private final ApiService apiService;
    private final PetRepository petRepository;

    // 추천된 Pet ID 저장
    private final Map<Long, Set<Long>> userRecommendedPets = new HashMap<>();

    public Map<String, Object> recommendPet(User user) throws IOException {
        try {
            // Step 1: 사용자 선호 기준 가져오기
            String size = user.getPreferredSize();
            String personality = user.getPreferredPersonality();
            Integer exerciseLevel = user.getPreferredExerciseLevel();

//            // Step 2: 보호소에서 등록된 모든 반려동물 데이터 가져오기
//            List<Pet> allPets = petRepository.findAll();
//            // Step 3: Pet 데이터를 AI에 전달하기 위한 포맷으로 변환
//            String petDescriptions = allPets.stream()
//                    .map(pet -> String.format(
//                            "Id: %s, Size: %s, Personality: %s, Exercise Level: %s",
//                            pet.getId(), pet.getSize(), pet.getPersonality(), pet.getExerciseLevel()))
//                    .collect(Collectors.joining("\n"));

            // Step 2: 사용자가 이미 추천받은 Pet ID 확인
            Set<Long> excludedPetIds = userRecommendedPets.getOrDefault(user.getId(), new HashSet<>());

            // Step 3: 보호소에서 등록된 Pet 중 제외된 ID를 필터링
            List<Pet> filteredPets = petRepository.findAll().stream()
                    .filter(pet -> !excludedPetIds.contains(pet.getId()))
                    .collect(Collectors.toList());
            if (filteredPets.isEmpty()) {
                throw new BaseException(ErrorCode.NO_PETS_AVAILABLE);
            }

            // Step 4: 필터링된 Pet 데이터를 AI에 전달
            String petDescriptions = filteredPets.stream()
                    .map(pet -> String.format(
                            "Id: %s, Size: %s, Personality: %s, Exercise Level: %s",
                            pet.getId(), pet.getSize(), pet.getPersonality(), pet.getExerciseLevel()))
                    .collect(Collectors.joining("\n"));

            String prompt = String.format(
                    "User prefers: Size: %s, Personality: %s, Exercise Level: %s." +
                            "\nAvailable pets:\n%s\nPlease recommend only one pet that perfectly suits to the all of user's preferences. Size should be equal. id should be said as ID: ?. Answer in Korean.",
                    size, personality, exerciseLevel, petDescriptions);

            // Step 5: OpenAI 호출
            String aiResponseContent = apiService.getRecommendation(prompt);  // aiResponseContent로 바로 처리;
            String recommendedId = extractRecommendedId(aiResponseContent);

            if (recommendedId == null) {
                throw new BaseException(ErrorCode.RECOMMENDATION_FAILED);
            }

            // Step 5: 추천된 반려동물 정보 가져오기
            Long petId = Long.valueOf(recommendedId);
            Pet recommendedPet = petRepository.findById(Long.valueOf(recommendedId))
                    .orElseThrow(() -> new BaseException(ErrorCode.PET_NOT_FOUND));

            // 추천된 id를 user에 따라 저장
            excludedPetIds.add(petId);
            userRecommendedPets.put(user.getId(), excludedPetIds);

            // petResponseDTO로 변환
            PetResponseDTO petResponseDTO = PetResponseDTO.fromEntity(recommendedPet); //dto에 fromEntity 존재, 이걸로 정적 팩토리 메서드 사용

            Map<String, Object> result = new HashMap<>();
            result.put("content", aiResponseContent); // AI 응답의 content
            result.put("petId", petResponseDTO.getPetId()); // 추천된 Pet의 ID
            result.put("pet", petResponseDTO); // Pet 정보

            return result;
        } catch (IOException e) {
            throw new BaseException(ErrorCode.AI_COMMUNICATION_ERROR);
        }
    }

    private String extractRecommendedId(String response) {
        Pattern pattern = Pattern.compile("ID:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}