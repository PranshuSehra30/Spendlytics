package com.pranshudev.spendlytics.service;

import com.pranshudev.spendlytics.dto.AuthDTO;
import com.pranshudev.spendlytics.dto.ProfileDTO;
import com.pranshudev.spendlytics.entity.ProfileEntity;
import com.pranshudev.spendlytics.repository.ProfileRepository;
import com.pranshudev.spendlytics.util.jwtUtil;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class ProfileService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final jwtUtil JwtUtil;
    private final ProfileRepository profileRepository;
    public ProfileDTO registerProfile(ProfileDTO profileDTO){

        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);

        // Send activation email
        String activationUrl = "http://localhost:8080/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = " Activate Your Spendlytics Account";
        // Create an HTML body with a clickable link or button
        String body = "<html><body>" +
                "<h3>Welcome to Spendlytics!</h3>" +
                "<p>Please click the link below to activate your account:</p>" +
                "<a href=\"" + activationUrl + "\" style=\"background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;\">Activate Account</a>" +
                "<p>If the button doesn't work, Just Click this URL: " + activationUrl + "</p>" +
                "</body></html>";

        emailService.sendHtmlEmail(newProfile.getEmail(), subject, body);
        return   toDTO(newProfile);
    }


    public ProfileEntity toEntity(ProfileDTO profileDTO){
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .fullName(profileDTO.getFullName())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .email(profileDTO.getEmail())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())

                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity){
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())


                .build();

    }

    public boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
                .map(profileEntity -> {
                    profileEntity.setIsActive(true);
                    profileRepository.save(profileEntity);
                    return true;
                }).orElse(false);


    }

    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();

        return profileRepository.findByEmail( authentication.getName()).orElseThrow(()-> new UsernameNotFoundException("Profile Not Found with email"+ authentication.getName()));



    }

    public ProfileDTO getPublicProfile(String email) {

        ProfileEntity profileEntity = profileRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Profile Not Found with email " + email));

        return modelMapper.map(profileEntity, ProfileDTO.class);
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
//genrate JWT token
            // Generate JWT token using JwtUtil
            String token = JwtUtil.generateToken(authDTO.getEmail());

            return Map.of(
                    "token",
                    token
                    ,
                    "user",getPublicProfile(authDTO.getEmail())
            );

        }catch (Exception e){
            throw new RuntimeException("Invalid username and password");
        }
    }
}
