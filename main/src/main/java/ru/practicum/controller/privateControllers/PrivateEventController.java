package ru.practicum.controller.privateControllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventDataDto;
import ru.practicum.dto.event.FullEventDto;
import ru.practicum.dto.event.InputNewEventDto;
import ru.practicum.dto.event.InputUpdateEventFromUserDto;
import ru.practicum.dto.request.ConfirmRequestDto;
import ru.practicum.dto.request.RequestOutputDto;
import ru.practicum.dto.request.RequestResultUpdateDto;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class PrivateEventController {

    EventService eventService;

    @GetMapping
    public List<EventDataDto> getEventsByUserId(@PathVariable Long userId,
                                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return eventService.getEventsByUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FullEventDto createEvent(@PathVariable @PositiveOrZero Long userId, @Valid @RequestBody InputNewEventDto inputNewEventDto) {
        return eventService.createEvent(userId, inputNewEventDto);
    }

    @GetMapping("/{eventId}")
    public FullEventDto getFullEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getFullEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public FullEventDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId, @Valid @RequestBody InputUpdateEventFromUserDto inputUpdateEventFromUserDto) {
        return eventService.updateEventByUser(userId, eventId, inputUpdateEventFromUserDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestOutputDto> getRequestsByUserId(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getRequestByUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestResultUpdateDto updateRequests(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody ConfirmRequestDto confirmRequestDto) {
        return eventService.updateRequests(userId, eventId, confirmRequestDto);
    }
}
