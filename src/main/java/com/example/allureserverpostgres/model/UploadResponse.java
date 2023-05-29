package com.example.allureserverpostgres.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadResponse {
    String fileName;
    String uuid;
}
