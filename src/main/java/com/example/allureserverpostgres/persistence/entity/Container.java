package com.example.allureserverpostgres.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
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
public class Container extends EntityWithUUID {

    private String name;
    private String befores; // TODO: Use JSON to store list
    private String afters; // TODO: Use JSON to store list
    private Long start;
    private Long stop;

    @CreatedDate
    private LocalDateTime createdDate;

    @ToString.Exclude
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "container_result",
            joinColumns = @JoinColumn(name = "container_uuid"),
            inverseJoinColumns = @JoinColumn(name = "result_uuid")
    )
    private Set<Result> results;

    @ManyToOne
    @JoinColumn(name = "execution_uuid")
    private Execution execution;
}
