package movielibrary.dtos.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static movielibrary.utils.StringConstants.*;

public record UserCreateDto(

        @NotBlank
        @Size(min = 3, max = 20, message = USERNAME_LIMITATION)
        String username,

        @NotBlank
        @Size(min = 3, max = 50, message = PASSWORD_LIMITATION)
        String password
) {
}
