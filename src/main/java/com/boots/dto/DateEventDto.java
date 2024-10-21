package com.boots.dto;

import java.time.LocalDateTime;

public class DateEventDto {
    private LocalDateTime eventDate;

    // Геттеры и сеттеры
    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
}
