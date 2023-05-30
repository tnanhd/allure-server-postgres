package com.example.allureserverpostgres.persistence.entity;

import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class FileAttachment extends EntityWithUUID {

    private String fileName;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] fileData;

    @CreatedDate
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "execution_uuid")
    private Execution execution;

    /**
     *
     * @param fileName file name
     * @param fileData file data
     */
    public FileAttachment(String fileName, byte[] fileData) {
        super.setUuid(UUID.randomUUID());
        this.fileName = fileName;
        this.fileData = fileData;
    }
}
