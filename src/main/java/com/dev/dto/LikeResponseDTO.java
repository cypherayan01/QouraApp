package com.dev.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeResponseDTO {

    private String id;
    private String targetId;
    private String targetType; 

    private Boolean isLike;

    private  String createdAt;
}
