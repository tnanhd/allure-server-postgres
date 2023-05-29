package com.example.allureserverpostgres.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Result extends EntityWithUUID {

    private String historyId;
    private String testCaseId;
    private String testCaseName;
    private String fullName;
    private String labels; // TODO: Json
    private String link; // TODO: Json
    private String name;
    private String status;
    private String stage;
    private String description;
    private String steps; // TODO: Json
    private String attachments; // TODO: Json
    private String parameters; // TODO: Json
    private Long start;
    private Long stop;

    @CreatedDate
    private LocalDateTime createdDate;

    @ToString.Exclude
    @JsonIgnore
    @ManyToMany(mappedBy = "results")
    private Set<Container> containers;
}
