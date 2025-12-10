package movielibrary.repositories;

import movielibrary.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findByTitle(String title);

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Long id);

}
