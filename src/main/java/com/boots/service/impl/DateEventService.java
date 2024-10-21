package com.boots.service.impl;


import com.boots.dto.DateEventDto;
import com.boots.entity.DateEvent;
import com.boots.entity.User;
import com.boots.repository.DateEventRepository;
import com.boots.repository.UserRepository;
import com.boots.service.IDateEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DateEventService implements IDateEventService {

    @Autowired
    private DateEventRepository dateEventRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public DateEvent createDateEventForUser(LocalDateTime eventDate, User user) {
        // Создаем новое событие
        DateEvent dateEvent = new DateEvent();
        dateEvent.setEventDate(eventDate);
        dateEvent.setUser(user);

        // Сохраняем событие
        return dateEventRepository.save(dateEvent);
    }

    @Override
    public List<DateEventDto> getEventDatesForUser(Long userId) {
        // Получаем события пользователя из репозитория
        List<DateEvent> events = dateEventRepository.findByUserId(userId);

        // Преобразуем события в DTO
        return events.stream()
                .map(event -> {
                    DateEventDto dto = new DateEventDto();
                    dto.setEventDate(event.getEventDate());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
