package ru.practicum.controller.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventDataDto;
import ru.practicum.dto.FullEventDto;
import ru.practicum.dto.InputUpdateEventDto;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AdminEventController {

    EventService eventService;

    @GetMapping
    public List<FullEventDto> getEventsById(@RequestParam("users") List<Long> users,
                                            @RequestParam("states") List<String> states,
                                            @RequestParam("categories") List<Long> categories,
                                            @RequestParam("rangeStart") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                            @RequestParam("rangeEnd") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                            @RequestParam(value = "from", defaultValue = "0") Integer from,
                                            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return eventService.getEventsByUserId(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public FullEventDto updateEvent(@PathVariable Long eventId, @RequestBody InputUpdateEventDto inputUpdateEventDto) {
        log.info("AdminEventController get request with eventId = {}, inputUpdateEventDto = {}", eventId, inputUpdateEventDto);
        return eventService.updateEvent(inputUpdateEventDto, eventId);
    }
}
