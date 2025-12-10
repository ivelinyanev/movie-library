package movielibrary.services;

import movielibrary.dtos.movies.MovieCreateDto;
import movielibrary.dtos.movies.MovieResponseDto;
import movielibrary.dtos.movies.MovieUpdateDto;

import java.util.List;

public interface MovieService {

    List<MovieResponseDto> getAll();

    MovieResponseDto getById(Long id);

    MovieResponseDto getByTitle(String title);

    MovieResponseDto create(MovieCreateDto dto);

    MovieResponseDto update(Long id, MovieUpdateDto dto);

    void delete(Long id);

}
