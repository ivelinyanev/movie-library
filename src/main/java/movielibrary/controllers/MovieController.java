package movielibrary.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import movielibrary.dtos.movies.MovieCreateDto;
import movielibrary.dtos.movies.MovieResponseDto;
import movielibrary.dtos.movies.MovieUpdateDto;
import movielibrary.mappers.MovieMapper;
import movielibrary.models.Movie;
import movielibrary.services.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Movies")
public class MovieController {

    private final MovieService movieService;
    private final MovieMapper mapper;

    @Operation(
            summary = "Get all movies"
    )
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<MovieResponseDto>> getAll() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        movieService.getAll()
                                .stream()
                                .map(mapper::toResponseDto)
                                .toList()
                );
    }

    @Operation(
            summary = "Get a movie by id"
    )
    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MovieResponseDto> getById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toResponseDto(movieService.getById(id)));
    }

    @Operation(
            summary = "Get a movie by title"
    )
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MovieResponseDto> getByTitle(@RequestParam String title) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toResponseDto(movieService.getByTitle(title)));
    }

    @Operation(
            summary = "Create a movie",
            description = "Creates a movie and calls external API to set movie rating asynchronously"
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponseDto> create(@Valid @RequestBody MovieCreateDto dto) {
        Movie movie = movieService.create(mapper.toMovie(dto));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResponseDto(movie));
    }

    @Operation(
            summary = "Update a movie"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponseDto> update(@PathVariable Long id, @Valid @RequestBody MovieUpdateDto dto) {
        Movie movie = movieService.update(id, mapper.toMovie(dto));

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(mapper.toResponseDto(movie));
    }

    @Operation(
            summary = "Delete a movie"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movieService.delete(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
