package ru.practicum.controller.generalController;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.event.EventDataDto;
import ru.practicum.dto.event.FullEventDto;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/events")
public class EventController {

    private EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventDataDto> getAllEvents(@RequestParam(required = false) String text,
                                                 @RequestParam(required = false) List<Long> categories,
                                                 @RequestParam(required = false) Boolean paid,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                 @RequestParam(required = false) boolean onlyAvailable,
                                                 @RequestParam(required = false) String sort,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size,
                                                 HttpServletRequest httpRequest) {
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, httpRequest);
    }

    @GetMapping("/{id}")
    public FullEventDto getEventById(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        return eventService.getEventById(id, httpServletRequest);
    }

    @GetMapping("/comments/{eventId}")
    public List<CommentDto> getCommentsByEventId(@PositiveOrZero @PathVariable Long eventId,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getAllCommentsByEvent(eventId, from, size);
    }
}
