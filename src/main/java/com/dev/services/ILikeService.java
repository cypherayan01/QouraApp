package com.dev.services;

import com.dev.dto.LikeRequestDTO;
import com.dev.dto.LikeResponseDTO;

import reactor.core.publisher.Mono;

public interface ILikeService {

    Mono<LikeResponseDTO> createLike(LikeRequestDTO likeRequestDTO);
    Mono<LikeResponseDTO> countLikesByTargetIdAndTargetType(String targetId, String targetType);
    Mono<LikeResponseDTO> countDisLikesByTargetIdAndTargetType(String targetId, String targetType);
    Mono<LikeResponseDTO> toggleLike(String targetId, String targetType, Boolean isLike);
}
