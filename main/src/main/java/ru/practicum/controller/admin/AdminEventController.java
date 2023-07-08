package ru.practicum.controller.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.FullEventDto;
import ru.practicum.dto.event.InputUpdateEventDto;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class AdminEventController {

    EventService eventService;

    @GetMapping
    public List<FullEventDto> getEventsById(@RequestParam(value = "users", required = false) List<Long> users,
                                            @RequestParam(value = "states", required = false) List<String> states,
                                            @RequestParam(value = "categories", required = false) List<Long> categories,
                                            @RequestParam(value = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                            @RequestParam(value = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                            @RequestParam(value = "from", defaultValue = "0") Integer from,
                                            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return eventService.getEventsByUserId(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public FullEventDto updateEvent(@PathVariable Long eventId, @Valid @RequestBody InputUpdateEventDto inputUpdateEventDto) {
        log.info("AdminEventController get request with eventId = {}, inputUpdateEventDto = {}", eventId, inputUpdateEventDto);
        return eventService.updateEvent(inputUpdateEventDto, eventId);
    }
}
