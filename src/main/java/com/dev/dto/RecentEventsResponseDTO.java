package com.dev.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecentEventsResponseDTO {
    private List<RecentEventDTO> events;
    private long totalCount;
    private TimeRangeDTO timeRange;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TimeRangeDTO {
        private LocalDateTime from;
        private LocalDateTime to;
    }
}