package movielibrary.dtos.users;

import movielibrary.models.Role;

import java.util.Set;

public record UserResponseDto(
        Long id,
        String username,
        Set<Role> roles
) {
}
