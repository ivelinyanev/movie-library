package movielibrary.mappers;

import movielibrary.dtos.users.UserCreateDto;
import movielibrary.dtos.users.UserResponseDto;
import movielibrary.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(UserCreateDto dto) {
        return new User(
                dto.username(),
                dto.password()
        );
    }

    public UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(
                user.getUsername(),
                user.getRoles()
        );
    }


}
