package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.ShortUser;
import ru.practicum.model.User;

@UtilityClass
public class UserMapper {
    public User toUser(ShortUser shortUser) {
        User user = new User();
        user.setEmail(shortUser.getEmail());
        user.setName(shortUser.getName());
        return user;
    }
}
