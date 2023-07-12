package ru.practicum.controller.admin;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.ShortUser;
import ru.practicum.model.User;
import ru.practicum.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public List<User> getUsersById(@RequestParam(value = "ids", required = false) List<Long> ids,
                            @RequestParam(value = "size", defaultValue = "10") Integer size,
                            @RequestParam(value = "from", defaultValue = "0") Integer from) {
        return userService.getUsersById(ids, size, from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody ShortUser shortUser) {
        return userService.addUser(shortUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }
}
