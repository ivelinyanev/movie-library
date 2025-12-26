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

import java.util.HashMap;
import java.util.Map;
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
            summary = "Get token",
            description = "Provide credentials to get JWT token"
    )
    @PostMapping("/token")
    public ResponseEntity<?> token(@Valid @RequestBody LoginUserDto dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );

        User user = userService.getEntityByUsername(dto.username());

        String token = jwt.generateToken(
                user.getUsername(),
                user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet())
        );

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("user", user.getUsername());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokenMap);
    }
}
