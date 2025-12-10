package movielibrary.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import movielibrary.dtos.users.UserCreateDto;
import movielibrary.dtos.users.UserResponseDto;
import movielibrary.dtos.users.UserUpdateDto;
import movielibrary.security.JwtUtils;
import movielibrary.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    /* ------------------------- Public part ------------------------- */

    @PostMapping("/public/users/create")
    public ResponseEntity<?> create(@Valid @RequestBody UserCreateDto dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.create(dto));
    }

    /* ------------------------- Private part ------------------------- */

    @PutMapping("/private/users")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponseDto> update(@Valid @RequestBody UserUpdateDto dto) {
        UserResponseDto response = userService.update(dto);

        /*
            after an update, regenerate token
         */
        String token = jwtUtils.regenerateToken(
                response.username(),
                response.roles().stream().map(r -> r.getName().name()).collect(Collectors.toSet())
        );

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .header("Set-Cookie", cookie.toString())
                .body(response);
    }

    @DeleteMapping("/private/users")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> delete() {
        userService.delete();

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    /* ------------------------- Admin part ------------------------- */

    @GetMapping("/admin/users/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getById(id));
    }

    @GetMapping("/admin/users/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getByUsername(@PathVariable String username) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getByUsername(username));
    }


    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAll() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getAll());
    }

    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminDelete(@PathVariable Long id) {
        userService.adminDelete(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
