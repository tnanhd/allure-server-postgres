package com.example.allureserverpostgres.dto.mapper;

import com.example.allureserverpostgres.dto.ResultDto;
import com.example.allureserverpostgres.persistence.entity.Result;

import java.util.ArrayList;
import java.util.UUID;

public class ResultMapper {
    public static Result fromDto(ResultDto resultDto) {
        Result result = Result.builder()
                .historyId(resultDto.getHistoryId())
                .testCaseId(resultDto.getTestCaseId())
                .testCaseName(resultDto.getTestCaseName())
                .fullName(resultDto.getFullName())
                .name(resultDto.getName())
                .status(resultDto.getStatus())
                .stage(resultDto.getStage())
                .description(resultDto.getDescription())
                .start(resultDto.getStart())
                .stop(resultDto.getStop())
                .build();
        result.setUuid(UUID.fromString(resultDto.getUuid()));
        return result;
    }

    public static ResultDto toDto(Result result) {
        return ResultDto.builder()
                .uuid(result.getUuidString())
                .historyId(result.getHistoryId())
                .testCaseId(result.getTestCaseId())
                .testCaseName(result.getTestCaseName())
                .fullName(result.getFullName())
                .labels(new ArrayList<>())
                .links(new ArrayList<>())
                .name(result.getName())
                .status(result.getStatus())
                .stage(result.getStage())
                .description(result.getDescription())
                .steps(new ArrayList<>())
                .attachments(new ArrayList<>())
                .parameters(new ArrayList<>())
                .start(result.getStart())
                .stop(result.getStop())
                .build();
    }
}
