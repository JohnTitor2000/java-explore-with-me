package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.user.ShortUser;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mappers.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    private final UserRepository userRepository;
    private final ParticipationRequestRepository prRepository;
    private final EventRepository eventRepository;

    public User addUser(ShortUser shortUser) {
        return userRepository.save(UserMapper.toUser(shortUser));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User with id=" + id + " was not found");
        }
        userRepository.deleteById(id);
    }

    public List<User> getUsersById(List<Long> ids, Integer size, Integer from) {
        Integer resultFrom = from.equals(0) ? 0 : from - 1;
        List<Long> resultIds = ids == null ? Collections.emptyList() : ids;
        if (resultIds.isEmpty()) {
            return userRepository.findAll().stream().skip(resultFrom).limit(size).collect(Collectors.toList());
        } else {
            return  userRepository.findAllByIdIn(ids);
        }
    }
}
