package com.example.allureserverpostgres.dto.mapper;

import com.example.allureserverpostgres.dto.ExecutionDto;
import com.example.allureserverpostgres.persistence.entity.Execution;

import java.util.UUID;

public class ExecutionMapper {
    public static Execution fromDto(ExecutionDto executionDto) {
        Execution execution = Execution.builder()
                .originalFileName(executionDto.getOriginalFileName())
                .name(executionDto.getName())
                .type(executionDto.getType())
                .taskName(executionDto.getTaskName())
                .buildName(executionDto.getBuildName())
                .projectPath(executionDto.getProjectPath())
                .projectVersion(executionDto.getProjectVersion())
                .build();
        execution.setUuid(UUID.fromString(executionDto.getUuid()));
        return execution;
    }

    public static ExecutionDto toDto(Execution execution) {
        return ExecutionDto.builder()
                .name(execution.getName())
                .type(execution.getType())
                .taskName(execution.getTaskName())
                .buildName(execution.getBuildName())
                .projectPath(execution.getProjectPath())
                .projectVersion(execution.getProjectVersion())
                .build();
    }
}
