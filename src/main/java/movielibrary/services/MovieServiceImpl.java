package movielibrary.services;

import lombok.RequiredArgsConstructor;
import movielibrary.enums.Status;
import movielibrary.exceptions.DuplicateEntityException;
import movielibrary.exceptions.EntityNotFoundException;
import movielibrary.models.Movie;
import movielibrary.repositories.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final RatingEnrichmentService ratingEnrichmentService;

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getAll() {

        return movieRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Movie getById(Long id) {

        return movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie", "id", String.valueOf(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Movie getByTitle(String title) {

        return movieRepository.findByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException("Movie", "title", title));
    }

    @Override
    @Transactional
    public Movie create(Movie movie) {
        if (hasDuplicate(movie.getTitle())) {
            throw new DuplicateEntityException("Movie", "title", movie.getTitle());
        }

        movie.setStatus(Status.PENDING);
        Movie saved = movieRepository.save(movie);

        ratingEnrichmentService.enrichRating(saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public Movie update(Long id, Movie movie) {
        Movie updateMovie = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie", "id", String.valueOf(id)));

        if (hasDuplicate(movie.getTitle(), id)) {
            throw new DuplicateEntityException("Movie", "title", movie.getTitle());
        }

        if (movie.getTitle() != null) updateMovie.setTitle(movie.getTitle());
        if (movie.getDirector() != null) updateMovie.setDirector(movie.getDirector());
        if (movie.getReleaseYear() != null) updateMovie.setReleaseYear(movie.getReleaseYear());
        if (movie.getRating() != null) updateMovie.setRating(movie.getRating());

        return movieRepository.saveAndFlush(movie);
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
