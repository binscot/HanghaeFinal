package com.example.hanghaefinal.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlarmResponseDto {

    private String alarmId;
    private String type;    // NEWPARAGRAPH, COMPLETEPOST, LIKEPARAGRAPH, LIKEPOST
    private String message;
    private Boolean isRead;
//    private String chatRoomId;
    private String postId;
    private String alarmTargetId;
}