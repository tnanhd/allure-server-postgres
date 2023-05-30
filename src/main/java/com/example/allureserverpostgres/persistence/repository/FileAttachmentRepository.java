package com.example.allureserverpostgres.persistence.repository;

import com.example.allureserverpostgres.persistence.entity.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileAttachmentRepository extends JpaRepository<FileAttachment, UUID> {
}
