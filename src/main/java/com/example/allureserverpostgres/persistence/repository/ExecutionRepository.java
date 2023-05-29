package com.example.allureserverpostgres.persistence.repository;

import com.example.allureserverpostgres.persistence.entity.Execution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExecutionRepository extends JpaRepository<Execution, UUID> {
}
