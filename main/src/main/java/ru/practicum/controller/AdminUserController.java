package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ShortUser;
import ru.practicum.model.User;
import ru.practicum.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public List<User> getUsersById(@RequestParam(value = "ids", required = false) List<Long> ids,
                            @RequestParam(value = "size", defaultValue = "10") Integer size,
                            @RequestParam(value = "from", defaultValue = "0") Integer from) {
        return userService.getUserById(ids, size, from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody ShortUser shortUser) {
        return userService.addUser(shortUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }
}
