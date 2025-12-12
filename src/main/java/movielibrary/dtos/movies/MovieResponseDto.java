package movielibrary.dtos.movies;

import movielibrary.enums.Status;

import java.time.Year;

public record MovieResponseDto(
        String title,
        String director,
        Year releaseYear,
        Double rating,
        Status status
) {
}
