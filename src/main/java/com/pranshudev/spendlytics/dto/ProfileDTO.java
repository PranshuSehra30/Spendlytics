package com.pranshudev.spendlytics.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO {

    private Long  id;
    private String fullName;



    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String profileImageUrl;

    private String icon;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
