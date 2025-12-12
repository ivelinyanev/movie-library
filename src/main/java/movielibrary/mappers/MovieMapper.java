package movielibrary.mappers;

import movielibrary.dtos.movies.MovieCreateDto;
import movielibrary.dtos.movies.MovieResponseDto;
import movielibrary.dtos.movies.MovieUpdateDto;
import movielibrary.models.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toMovie(MovieCreateDto dto) {
        return new Movie(
                dto.title(),
                dto.director(),
                dto.releaseYear()
        );
    }

    public Movie toMovie(MovieUpdateDto dto) {
        return new Movie(
                dto.title(),
                dto.director(),
                dto.releaseYear()
        );
    }

    public MovieResponseDto toResponseDto(Movie movie) {
        return new MovieResponseDto(
                movie.getTitle(),
                movie.getDirector(),
                movie.getReleaseYear(),
                movie.getRating(),
                movie.getStatus()
        );
    }

}
