package com.example.allureserverpostgres.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
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

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private List<String> befores;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private List<String> afters;

    private Long start;
    private Long stop;

    /**
     * Virtual containers should not be generated to json files
     */
    private Boolean isVirtual;

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
