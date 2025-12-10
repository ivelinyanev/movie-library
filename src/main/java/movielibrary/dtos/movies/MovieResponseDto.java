package movielibrary.dtos.movies;

import java.time.Year;

public record MovieResponseDto(
        String title,
        String director,
        Year releaseYear,
        Double rating
) {
}
