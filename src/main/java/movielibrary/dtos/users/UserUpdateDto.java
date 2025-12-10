package movielibrary.dtos.users;

import jakarta.validation.constraints.Size;

import static movielibrary.utils.StringConstants.PASSWORD_LIMITATION;
import static movielibrary.utils.StringConstants.USERNAME_LIMITATION;

public record UserUpdateDto (

        @Size(min = 3, max = 20, message = USERNAME_LIMITATION)
        String username,

        @Size(min = 3, max = 50, message = PASSWORD_LIMITATION)
        String password
) {
}
