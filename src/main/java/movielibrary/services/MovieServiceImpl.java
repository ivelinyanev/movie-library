package movielibrary.services;

import lombok.RequiredArgsConstructor;
import movielibrary.dtos.movies.MovieCreateDto;
import movielibrary.dtos.movies.MovieResponseDto;
import movielibrary.dtos.movies.MovieUpdateDto;
import movielibrary.exceptions.DuplicateEntityException;
import movielibrary.exceptions.EntityNotFoundException;
import movielibrary.mappers.MovieMapper;
import movielibrary.models.Movie;
import movielibrary.repositories.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponseDto> getAll() {

        return movieRepository
                .findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponseDto getById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie", "id", String.valueOf(id)));

        return mapper.toResponseDto(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponseDto getByTitle(String title) {
        Movie movie = movieRepository.findByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException("Movie", "title", title));

        return mapper.toResponseDto(movie);
    }

    @Override
    @Transactional
    public MovieResponseDto create(MovieCreateDto dto) {
        if (hasDuplicate(dto.title())) {
            throw new DuplicateEntityException("Movie", "title", dto.title());
        }

        Movie movie = mapper.toMovie(dto);
        movieRepository.save(movie);

        return mapper.toResponseDto(movie);
    }

    @Override
    @Transactional
    public MovieResponseDto update(Long id, MovieUpdateDto dto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie", "id", String.valueOf(id)));

        if(hasDuplicate(dto.title(), id)) {
            throw new DuplicateEntityException("Movie", "title", dto.title());
        }

        if (dto.title() != null) movie.setTitle(dto.title());
        if (dto.director() != null) movie.setDirector(dto.director());
        if (dto.releaseYear() != null) movie.setReleaseYear(dto.releaseYear());
        if (dto.rating() != null) movie.setRating(dto.rating());

        movieRepository.saveAndFlush(movie);
        return mapper.toResponseDto(movie);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie", "id", String.valueOf(id)));

        movieRepository.delete(movie);
    }

    /*
        When creating
     */
    private boolean hasDuplicate(String title) {
        return movieRepository.existsByTitle(title);
    }

    /*
        When updating
     */
    private boolean hasDuplicate(String title, Long id) {
        if (title == null || id == null) return false;

        return movieRepository.existsByTitleAndIdNot(title, id);
    }
}
