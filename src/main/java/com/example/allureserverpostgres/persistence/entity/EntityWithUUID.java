package com.example.allureserverpostgres.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
@Setter
@Getter
public class EntityWithUUID {
    @Id @Type(type = "pg-uuid")
    private UUID uuid;

    public EntityWithUUID() {
        this.uuid = UUID.randomUUID();
    }

    public String getUuidString() {
        return uuid.toString();
    }
}
