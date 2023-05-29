package com.example.allureserverpostgres.dto;

import jdk.jfr.Label;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionDto {
    private String uuid;
    private String originalFileName;
    private String name;
    private String type;
    private String taskName;
    private String buildName;
    private String projectPath;
    private String projectVersion;
}
