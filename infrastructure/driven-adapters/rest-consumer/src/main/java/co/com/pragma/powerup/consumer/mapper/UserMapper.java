package co.com.pragma.powerup.consumer.mapper;

import co.com.pragma.powerup.consumer.dto.UserResponse;
import co.com.pragma.powerup.model.user.User;

public class UserMapper {
    public static User toDomain(UserResponse resp) {
        return new User(resp.getIdCard(),resp.getEmailAddress());
    }
}
