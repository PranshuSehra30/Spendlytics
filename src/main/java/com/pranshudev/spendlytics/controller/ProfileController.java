package com.pranshudev.spendlytics.controller;


import com.pranshudev.spendlytics.dto.AuthDTO;
import com.pranshudev.spendlytics.dto.ProfileDTO;
import com.pranshudev.spendlytics.service.ProfileService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    public final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO){
        ProfileDTO registerProfile = profileService.registerProfile(profileDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(registerProfile);

    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam("token") String activationToken){

        boolean isActivated = profileService.activateProfile(activationToken);
        if(isActivated){
            return ResponseEntity.status(HttpStatus.OK).body("Activated");

        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation Token not found or Already Activated");
        }
    }

    @GetMapping("/test")
    public String test(){
        return "testdone";
    }



    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody AuthDTO authDTO) {

        try {
            // Check if profile is active
            if (!profileService.isAccountActive(authDTO.getEmail())) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "message", "Account is not active. Please activate your account first."
                        ));
            }

            // Authenticate & generate token
            Map<String, Object> response =
                    profileService.authenticateAndGenerateToken(authDTO);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", e.getMessage()
                    ));
        }
    }


}
