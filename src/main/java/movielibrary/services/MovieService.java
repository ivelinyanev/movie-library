package movielibrary.services;

import movielibrary.models.Movie;

import java.util.List;

public interface MovieService {

    List<Movie> getAll();

    Movie getById(Long id);

    Movie getByTitle(String title);

    Movie create(Movie movie);

    Movie update(Long id, Movie movie);

    void delete(Long id);

}
