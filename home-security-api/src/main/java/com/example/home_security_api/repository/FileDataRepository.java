package com.example.home_security_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.home_security_api.model.FileData;

public interface FileDataRepository extends JpaRepository<FileData, Long>{
    boolean existsByName(String name);
}
