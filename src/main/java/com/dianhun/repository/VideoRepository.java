package com.dianhun.repository;

import com.dianhun.entity.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<VideoEntity, Integer> {
}
