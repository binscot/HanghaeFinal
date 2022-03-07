package com.example.hanghaefinal.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadgeResponseDto {
    private String badge;

    public BadgeResponseDto(String badgeName) {
        this.badge=badgeName;
    }
}
