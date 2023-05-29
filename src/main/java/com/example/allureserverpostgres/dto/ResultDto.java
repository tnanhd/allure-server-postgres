package com.example.allureserverpostgres.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultDto {
    private String uuid;
    private String historyId;
    private String testCaseId;
    private String testCaseName;
    private String fullName;
    private List<Label> labels;
    private List<String> links;
    private String name;
    private String status;
    private String stage;
    private String description;
    private List<String> steps;
    private List<String> attachments;
    private List<String> parameters;
    private Long start;
    private Long stop;

    @Data
    public static class Label {
        private String name;
        private String value;
    }
}
