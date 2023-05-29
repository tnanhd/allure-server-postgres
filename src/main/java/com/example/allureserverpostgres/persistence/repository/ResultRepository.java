package com.example.allureserverpostgres.persistence.repository;

import com.example.allureserverpostgres.persistence.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResultRepository extends JpaRepository<Result, UUID> {
}
