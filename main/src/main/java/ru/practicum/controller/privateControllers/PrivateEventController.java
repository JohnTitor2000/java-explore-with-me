package ru.practicum.controller.privateControllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
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

    private EventService eventService;

    @GetMapping
    public List<EventDataDto> getEventsByUserId(@PathVariable Long userId,
                                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return eventService.getEventsByUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FullEventDto createEvent(@PositiveOrZero @PathVariable Long userId,
                                    @Valid @RequestBody InputNewEventDto inputNewEventDto) {
        return eventService.createEvent(userId, inputNewEventDto);
    }

    @GetMapping("/{eventId}")
    public FullEventDto getFullEvent(@PositiveOrZero @PathVariable Long userId,
                                     @PositiveOrZero @PathVariable Long eventId) {
        return eventService.getFullEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public FullEventDto updateEvent(@PositiveOrZero @PathVariable Long userId,
                                    @PositiveOrZero @PathVariable Long eventId,
                                    @Valid @RequestBody InputUpdateEventFromUserDto inputUpdateEventFromUserDto) {
        return eventService.updateEventByUser(userId, eventId, inputUpdateEventFromUserDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestOutputDto> getRequestsByUserId(@PositiveOrZero @PathVariable Long userId,
                                                      @PositiveOrZero @PathVariable Long eventId) {
        return eventService.getRequestByUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestResultUpdateDto updateRequests(@PositiveOrZero @PathVariable Long userId,
                                                 @PositiveOrZero @PathVariable Long eventId,
                                                 @RequestBody ConfirmRequestDto confirmRequestDto) {
        return eventService.updateRequests(userId, eventId, confirmRequestDto);
    }

    @PostMapping("/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@Valid @RequestBody NewCommentDto newCommentDto,
                                 @PositiveOrZero @PathVariable Long userId,
                                 @PositiveOrZero @PathVariable Long eventId) {
        return eventService.addComment(eventId, userId, newCommentDto);
    }

    @PatchMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@Valid @RequestBody UpdateCommentDto updateCommentDto,
                                    @PositiveOrZero @PathVariable Long userId,
                                    @PositiveOrZero @PathVariable Long commentId) {
        return eventService.updateCommentByOwner(updateCommentDto, userId, commentId);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeComment(@PositiveOrZero @PathVariable Long userId,
                              @PositiveOrZero @PathVariable Long commentId) {
        eventService.removeCommentByOwner(commentId, userId);
    }
}
