package com.dev.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeRequestDTO {


    @NotBlank(message = "Target ID cannot be blank")
    private String targetId;
    @NotBlank(message = "Target Type cannot be blank")
    private String targetType; 
    @NotBlank(message = "Like status cannot be blank")
    private Boolean isLike;


}
