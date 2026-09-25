package com.sat.tracker.dto;

import com.sat.tracker.model.Hod;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HodProfileDto {
    private Long id;
    private String name;
    private String hodId;
    private String branch;
    private String email;
    private String username;

    public static HodProfileDto from(Hod h) {
        return HodProfileDto.builder()
                .id(h.getId())
                .name(h.getName())
                .hodId(h.getHodId())
                .branch(h.getBranch())
                .email(h.getUser().getEmail())
                .username(h.getUser().getUsername())
                .build();
    }
}
