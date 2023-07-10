package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatisticService statisticService;

    @Transactional
    public CompilationDto add(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
            if (events.isEmpty() || events.size() != newCompilationDto.getEvents().size()) {
                throw new NotFoundException("Event not found");
            }
            compilation.setEvents(new HashSet<>(events));
        }
        Compilation savedCompilation = compilationRepository.save(compilation);
        if (compilation.getEvents() != null && !compilation.getEvents().isEmpty()) {
            statisticService.setStatistic(new ArrayList<>(savedCompilation.getEvents()));
            savedCompilation.getEvents().stream().forEach(o -> o.setConfirmedRequest(participationRequestRepository.getConfirmedRequestsByEventId(o.getId())));
        }
        return CompilationMapper.toCompilationDto(savedCompilation);
    }

    @Transactional
    public void remove(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }
        compilationRepository.deleteById(compId);
    }


    public CompilationDto update(Long compId, UpdateCompilationDto updateCompilationDto) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }
        Compilation modifiedCompilation = compilationRepository.findById(compId).get();
        if (updateCompilationDto.getPinned() != null) {
            modifiedCompilation.setPinned(updateCompilationDto.getPinned());
        }
        if (updateCompilationDto.getTitle() != null && !updateCompilationDto.getTitle().isBlank()) {
            modifiedCompilation.setTitle(updateCompilationDto.getTitle());
        }
        if (updateCompilationDto.getEvents() != null) {
            modifiedCompilation.setEvents(updateCompilationDto.getEvents().stream()
                    .map(o -> eventRepository.findById(o).orElseThrow(() -> new NotFoundException("Event with id=" + o + "not found"))).collect(Collectors.toSet()));
        }
        Compilation updatedCompilation = compilationRepository.save(modifiedCompilation);
        if (!updatedCompilation.getEvents().isEmpty() || updatedCompilation.getEvents() != null) {
            statisticService.setStatistic(new ArrayList<>(updatedCompilation.getEvents()));
            updatedCompilation.getEvents().forEach(o -> o.setConfirmedRequest(participationRequestRepository.getConfirmedRequestsByEventId(o.getId())));
        }
        return CompilationMapper.toCompilationDto(updatedCompilation);
    }

    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageable);
        for (Compilation compilation : compilations) {
            if (compilation.getEvents() != null && !compilation.getEvents().isEmpty()) {
                statisticService.setStatistic(new ArrayList<>(compilation.getEvents()));
                compilation.getEvents().forEach(o -> o.setConfirmedRequest(participationRequestRepository.getConfirmedRequestsByEventId(o.getId())));
            }
        }
        return compilations.stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
    }

    public CompilationDto getCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + "was not found");
        }
        Compilation compilation = compilationRepository.findById(compId).get();
        if (compilation.getEvents() != null && !compilation.getEvents().isEmpty()) {
            statisticService.setStatistic(new ArrayList<>(compilation.getEvents()));
            compilation.getEvents().forEach(o -> o.setConfirmedRequest(participationRequestRepository.getConfirmedRequestsByEventId(o.getId())));
        }
        return CompilationMapper.toCompilationDto(compilation);
    }
}
