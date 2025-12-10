package movielibrary.services;

import movielibrary.dtos.users.UserCreateDto;
import movielibrary.dtos.users.UserResponseDto;
import movielibrary.dtos.users.UserUpdateDto;
import movielibrary.models.User;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getAll();

    UserResponseDto getById(Long id);

    UserResponseDto getByUsername(String username);

    User getEntityByUsername(String username);

    UserResponseDto create(UserCreateDto dto);

    UserResponseDto update(UserUpdateDto dto);

    void delete();

    void adminDelete(Long id);
}
