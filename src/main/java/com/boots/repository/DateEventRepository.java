package com.boots.repository;

import com.boots.entity.DateEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DateEventRepository extends JpaRepository<DateEvent, Long> {
    List<DateEvent> findByUserId(Long userId);
}
