package com.example.allureserverpostgres.controller;

import com.example.allureserverpostgres.model.UploadResponse;
import com.example.allureserverpostgres.persistence.entity.Execution;
import com.example.allureserverpostgres.service.ResultService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/result")
@Slf4j
public class ResultController {
    private final ResultService resultService;

    @SneakyThrows
    @PostMapping(consumes = {"multipart/form-data"})
    public UploadResponse uploadResults(@RequestParam MultipartFile allureResults) {
        // Unzip and save
        Path path = resultService.unzipAndStore(allureResults);
        log.info("File saved to file system '{}'", allureResults);
        return new UploadResponse(allureResults.getOriginalFilename(), path.getFileName().toString());
    }

    @SneakyThrows
    @GetMapping("/{uuid}")
    public UploadResponse getResults(@PathVariable String uuid) {
        Execution execution = resultService.getExecution(uuid);
        log.info("Files generated to file system '{}'", execution.getOriginalFileName());
        return new UploadResponse(execution.getOriginalFileName(), execution.getUuid().toString());
    }
}
