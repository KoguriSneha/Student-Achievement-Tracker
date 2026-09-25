package com.sat.tracker.dto;

import com.sat.tracker.model.Staff;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StaffProfileDto {
    private Long id;
    private String name;
    private String staffId;
    private String branch;
    private String section;
    private Integer year;
    private String email;
    private String username;

    public static StaffProfileDto from(Staff s) {
        return StaffProfileDto.builder()
                .id(s.getId())
                .name(s.getName())
                .staffId(s.getStaffId())
                .branch(s.getBranch())
                .section(s.getSection())
                .year(s.getYear())
                .email(s.getUser().getEmail())
                .username(s.getUser().getUsername())
                .build();
    }
}
