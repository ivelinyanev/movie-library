package movielibrary.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import movielibrary.dtos.users.LoginUserDto;
import movielibrary.mappers.UserMapper;
import movielibrary.models.User;
import movielibrary.security.JwtUtils;
import movielibrary.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwt;
    private final UserService userService;

    /* ------------------------- Public part ------------------------- */

    @Operation(
            summary = "Log in",
            description = "Log in generates and sets a JWT token in cookies"
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserDto dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );

        User user = userService.getEntityByUsername(dto.username());

        String token = jwt.generateToken(
                user.getUsername(),
                user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet())
        );

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(1 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Set-Cookie", cookie.toString())
                .build();
    }

    @Operation(
            summary = "Log out",
            description = "Log out sets an expired cookie, thus logging out the user"
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Set-Cookie", cookie.toString())
                .build();
    }
}
