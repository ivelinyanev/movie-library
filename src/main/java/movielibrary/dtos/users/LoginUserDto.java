package movielibrary.dtos.users;

import jakarta.validation.constraints.NotBlank;

public record LoginUserDto(
        @NotBlank
        String username,

        @NotBlank
        String password
) {
}
