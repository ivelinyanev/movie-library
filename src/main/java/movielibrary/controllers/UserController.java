package movielibrary.controllers;

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
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final UserMapper mapper;

    /* ------------------------- Public part ------------------------- */

    @PostMapping()
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserCreateDto dto) {
        User user = userService.create(mapper.toUser(dto));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResponseDto(user));
    }

    /* ------------------------- Private part ------------------------- */

    @PutMapping()
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

    @DeleteMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> delete() {
        userService.delete();

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    /* ------------------------- Admin part ------------------------- */
    @GetMapping()
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

    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toResponseDto(userService.getById(id)));
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getByUsername(@PathVariable String username) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toResponseDto(userService.getByUsername(username)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminDelete(@PathVariable Long id) {
        userService.adminDelete(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
