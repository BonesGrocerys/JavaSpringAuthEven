package com.boots.service;

import com.boots.dto.DateEventDto;
import com.boots.entity.DateEvent;
import com.boots.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface IDateEventService {
    DateEvent createDateEventForUser(LocalDateTime eventDate, User user);
    List<DateEventDto> getEventDatesForUser(Long userId);
}
