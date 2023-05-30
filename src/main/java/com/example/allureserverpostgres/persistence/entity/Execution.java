package com.example.allureserverpostgres.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Execution extends EntityWithUUID {

    private String originalFileName;
    private String name;
    private String type;
    private String taskName;
    private String buildName;
    private String projectPath;
    private String projectVersion;

    /**
     * Virtual execution should not be generated to json file
     */
    private Boolean isVirtual;

    @CreatedDate
    private LocalDateTime createdDate;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "execution")
    private Set<Container> containers;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "execution")
    private Set<FileAttachment> fileAttachments;
}
