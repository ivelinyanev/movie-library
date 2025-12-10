package movielibrary.dtos.users;

import movielibrary.models.Role;

import java.util.Set;

public record UserResponseDto(
        String username,
        Set<Role> roles
) {
}
