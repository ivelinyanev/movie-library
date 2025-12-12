package movielibrary.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import movielibrary.dtos.users.UserCreateDto;
import movielibrary.dtos.users.UserResponseDto;
import movielibrary.dtos.users.UserUpdateDto;
import movielibrary.mappers.UserMapper;
import movielibrary.models.User;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final UserMapper mapper;

    /* ------------------------- Public part ------------------------- */

    @Operation(
            summary = "Create a user"
    )
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserCreateDto dto) {
        User user = userService.create(mapper.toUser(dto));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResponseDto(user));
    }

    /* ------------------------- Private part ------------------------- */

    @Operation(
            summary = "Update a user",
            description = "Self update only"
    )
    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponseDto> selfUpdate(@Valid @RequestBody UserUpdateDto dto) {
        User user = userService.update(mapper.toUser(dto));

        /*
            after an update, regenerate token
         */
        String token = jwtUtils.regenerateToken(
                user.getUsername(),
                user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet())
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
                .body(mapper.toResponseDto(user));
    }

    @Operation(
            summary = "Delete a user",
            description = "User can self delete only"
    )
    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> delete() {
        userService.delete();

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    /* ------------------------- Admin part ------------------------- */

    @Operation(
            summary = "Get all users"
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAll() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        userService.getAll()
                                .stream()
                                .map(mapper::toResponseDto)
                                .toList()
                );
    }

    @Operation(
            summary = "Get a user by id"
    )
    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toResponseDto(userService.getById(id)));
    }

    @Operation(
            summary = "Get a user by username"
    )
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getByUsername(@PathVariable String username) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toResponseDto(userService.getByUsername(username)));
    }

    @Operation(
            summary = "Delete a user",
            description = "Admin can delete any user"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminDelete(@PathVariable Long id) {
        userService.adminDelete(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
