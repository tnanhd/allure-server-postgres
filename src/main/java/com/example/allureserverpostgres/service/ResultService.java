package com.example.allureserverpostgres.service;

import com.example.allureserverpostgres.dto.ContainerDto;
import com.example.allureserverpostgres.dto.ExecutionDto;
import com.example.allureserverpostgres.dto.mapper.ContainerMapper;
import com.example.allureserverpostgres.dto.mapper.ExecutionMapper;
import com.example.allureserverpostgres.dto.ResultDto;
import com.example.allureserverpostgres.dto.mapper.ResultMapper;
import com.example.allureserverpostgres.persistence.entity.Container;
import com.example.allureserverpostgres.persistence.entity.Execution;
import com.example.allureserverpostgres.persistence.entity.FileAttachment;
import com.example.allureserverpostgres.persistence.entity.Result;
import com.example.allureserverpostgres.persistence.repository.ContainerRepository;
import com.example.allureserverpostgres.persistence.repository.ExecutionRepository;
import com.example.allureserverpostgres.persistence.repository.FileAttachmentRepository;
import com.example.allureserverpostgres.persistence.repository.ResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.iopump.qa.util.FileUtil;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.nio.file.Files.isDirectory;

@Service
@Slf4j
public class ResultService {

    private final Path storagePath;
    private final ExecutionRepository executionRepository;
    private final ContainerRepository containerRepository;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final ResultRepository resultRepository;

    public ResultService(ExecutionRepository executionRepository, ContainerRepository containerRepository, FileAttachmentRepository fileAttachmentRepository, ResultRepository resultRepository) {
        this.executionRepository = executionRepository;
        this.containerRepository = containerRepository;
        this.fileAttachmentRepository = fileAttachmentRepository;
        this.resultRepository = resultRepository;
        this.storagePath = Paths.get("allure/results/");
    }

    public void deleteAll() throws IOException {
        FileUtils.deleteDirectory(storagePath.toFile());
    }

    /**
     * Check archive, unzip and save to the file system.
     * Directory with uuid name will contain archive content.
     *
     * @param allureResults input file from client
     * @return Directory that contains the archive's content.
     * @throws IOException IO Error
     */
    @NonNull
    public Path unzipAndStore(@NonNull MultipartFile allureResults) throws IOException {
        var archiveInputStream = allureResults.getInputStream();
        Path tmpResultDirectory = null;
        Path resultDirectory = null;
        try (InputStream io = archiveInputStream) {
            final String uuid = UUID.randomUUID().toString();
            tmpResultDirectory = storagePath.resolve(uuid + "_tmp");
            resultDirectory = storagePath.resolve(uuid);
            Files.createDirectories(resultDirectory);
            checkAndUnzipTo(io, tmpResultDirectory);

            ObjectMapper mapper = new ObjectMapper();
            String zipFileName = FilenameUtils.removeExtension(allureResults.getOriginalFilename());
            List<String> names = listAllFilesInFolder(tmpResultDirectory, zipFileName);

            List<ContainerDto> containerDtos = new ArrayList<>();
            Map<String, Result> allResults = new HashMap<>();
            List<FileAttachment> allAttachments = new ArrayList<>();
            var executionDtos = new ExecutionDto[1];

            Path finalTmpResultDirectory = tmpResultDirectory;
            names.forEach(name -> {
                try {
                    assert zipFileName != null;
                    if (name.contains("container")) {
                        File file = finalTmpResultDirectory.resolve(zipFileName).resolve(name).toFile();
                        ContainerDto dto = mapper.readValue(file, ContainerDto.class);
                        containerDtos.add(dto);
                    } else if (name.contains("result")) {
                        File file = finalTmpResultDirectory.resolve(zipFileName).resolve(name).toFile();
                        ResultDto dto = mapper.readValue(file, ResultDto.class);
                        allResults.put(dto.getUuid(), ResultMapper.fromDto(dto));
                    } else if (name.contains("executor")) {
                        File file = finalTmpResultDirectory.resolve(zipFileName).resolve(name).toFile();
                        executionDtos[0] = mapper.readValue(file, ExecutionDto.class);
                        executionDtos[0].setOriginalFileName(allureResults.getOriginalFilename());
                    } else if (name.contains("attachment")) {
                        var fileData = Files.readAllBytes(finalTmpResultDirectory.resolve(zipFileName).resolve(name));
                        var attachment = new FileAttachment(name, fileData);
                        allAttachments.add(attachment);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            ExecutionDto executionDto = executionDtos[0];
            Execution execution = null;
            if (executionDto != null) {
                execution = ExecutionMapper.fromDto(executionDto);
            }
            if (execution == null) {
                execution = new Execution();
                execution.setIsVirtual(true);
                execution.setOriginalFileName(allureResults.getOriginalFilename());
            }
            execution.setUuid(UUID.fromString(uuid));
            executionRepository.save(execution);

            for (var attachment : allAttachments) {
                attachment.setExecution(execution);
            }
            fileAttachmentRepository.saveAll(allAttachments);

            List<Container> containers = new ArrayList<>();
            for (var containerDto : containerDtos) {
                Container container = ContainerMapper.fromDto(containerDto);
                container.setExecution(execution);
                containers.add(container);
                containerDto.getChildren()
                        .forEach(resultUuid -> {
                            var result = allResults.get(resultUuid);
                            if (container.getResults() == null) {
                                container.setResults(new HashSet<>());
                            }
                            container.getResults().add(result);
                        });

            }
            if (containers.isEmpty()) {
                Container container = new Container();
                container.setUuid(UUID.randomUUID());
                container.setResults(new HashSet<>(allResults.values()));
                container.setIsVirtual(true);
                container.setExecution(execution);
                containers.add(container);
            }

            resultRepository.saveAll(allResults.values());
            containerRepository.saveAll(containers);
        } catch (Exception ex) { //NOPMD
            if (resultDirectory != null) {
                // Clean on error
                FileUtils.deleteQuietly(resultDirectory.toFile());
            }
            if (tmpResultDirectory != null) {
                // Clean on error
                FileUtils.deleteQuietly(tmpResultDirectory.toFile());
            }
            throw ex; // And re-throw
        }
        log.info("Archive content saved to '{}'", resultDirectory);
        return resultDirectory;
    }

    private List<String> listAllFilesInFolder(Path tmpResultDirectory, String zipFileName) throws IOException {
        try (Stream<Path> stream = Files.list(tmpResultDirectory.resolve(zipFileName))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }

    private void checkAndUnzipTo(InputStream zipArchiveIo, Path unzipTo) throws IOException {
        ZipInputStream zis = new ZipInputStream(zipArchiveIo);
        byte[] buffer = new byte[1024];
        ZipEntry zipEntry = zis.getNextEntry();
        if (zipEntry == null) {
            throw new IllegalArgumentException("Passed InputStream is not a Zip Archive or empty");
        }
        while (zipEntry != null) {
            final Path newFile = fromZip(unzipTo, zipEntry);
            try (final OutputStream fos = Files.newOutputStream(newFile)) {
                int len;
                while ((len = zis.read(buffer)) > 0) { //NOPMD
                    fos.write(buffer, 0, len);
                }
            }
            log.info("Unzip new entry '{}'", newFile);
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();

        log.info("Unzipping successfully finished to '{}'", unzipTo);
    }

    private Path fromZip(Path unzipTo, ZipEntry zipEntry) {
        final Path entryPath = Paths.get(zipEntry.getName());
        final Path destinationFileOrDir = unzipTo.resolve(entryPath);

        if (isDirectory(destinationFileOrDir)) {
            FileUtil.createDir(destinationFileOrDir);
        } else {
            FileUtil.createFile(destinationFileOrDir);
        }

        return destinationFileOrDir;
    }

    public Execution getExecution(String uuid) throws IOException {
        Execution execution = executionRepository.findById(UUID.fromString(uuid))
                .orElseThrow(EntityNotFoundException::new);

        generateFiles(execution);

        return execution;
    }

    private void generateFiles(Execution execution) throws IOException {
        Path folderPath = storagePath
                .resolve(execution.getUuid().toString() + "_tmp")
                .resolve(FilenameUtils.removeExtension(execution.getOriginalFileName()));
        FileUtils.deleteDirectory(folderPath.toFile());
        Files.createDirectories(folderPath);
        ObjectMapper mapper = new ObjectMapper();

        if (execution.getIsVirtual().equals(false)) {
            ExecutionDto executionDto = ExecutionMapper.toDto(execution);
            mapper.writeValue(folderPath.resolve("executor.json").toFile(), executionDto);
        }

        var attachments = execution.getFileAttachments();
        for (var attachment : attachments) {
            var fileData = attachment.getFileData();
            var path = folderPath.resolve(attachment.getFileName());
            Files.write(path, fileData);
        }

        Set<Container> containers = execution.getContainers();
        for (var container : containers) {
            if (container.getIsVirtual().equals(false)) {
                ContainerDto containerDto = ContainerMapper.toDto(container);
                mapper.writeValue(folderPath
                                .resolve(container.getUuidString() + "-container.json")
                                .toFile(),
                        containerDto);
            }

            var results = container.getResults();
            for (var result : results) {
                var resultDto = ResultMapper.toDto(result);
                mapper.writeValue(folderPath
                                .resolve(result.getUuidString() + "-result.json")
                                .toFile(),
                        resultDto);
            }
        }

    }
}
