package movielibrary.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import movielibrary.dtos.movies.MovieCreateDto;
import movielibrary.dtos.movies.MovieResponseDto;
import movielibrary.dtos.movies.MovieUpdateDto;
import movielibrary.services.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/private/movies")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<MovieResponseDto>> getAll() {
        List<MovieResponseDto> response = movieService.getAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/private/movies/id/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MovieResponseDto> getById(@PathVariable Long id) {
        MovieResponseDto response = movieService.getById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/private/movies/title/{title}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MovieResponseDto> getByTitle(@PathVariable String title) {
        MovieResponseDto response = movieService.getByTitle(title);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/admin/movies")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponseDto> create(@Valid @RequestBody MovieCreateDto dto) {
        MovieResponseDto response = movieService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/admin/movies/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponseDto> update(@PathVariable Long id, @Valid @RequestBody MovieUpdateDto dto) {
        MovieResponseDto response = movieService.update(id, dto);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }

    @DeleteMapping("/admin/movies/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movieService.delete(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
