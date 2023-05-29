package com.example.allureserverpostgres.persistence.repository;

import com.example.allureserverpostgres.persistence.entity.Container;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContainerRepository extends JpaRepository<Container, UUID> {
}
