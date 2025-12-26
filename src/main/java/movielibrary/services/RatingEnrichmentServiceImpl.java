package movielibrary.services;

import movielibrary.dtos.imdb.ImdbResponseDto;
import movielibrary.enums.Status;
import movielibrary.models.Movie;
import movielibrary.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
public class RatingEnrichmentServiceImpl implements RatingEnrichmentService {

    @Value("${omdb.api.key}")
    private String key;

    private final RestClient client;
    private final MovieRepository repository;

    @Autowired
    public RatingEnrichmentServiceImpl(MovieRepository repository) {
        this.client = RestClient.create();
        this.repository = repository;
    }

    @Async
    @Transactional
    public void enrichRating(Long movieId) {
        Movie movie = repository.findById(movieId)
                .orElseThrow(() -> new IllegalStateException("Movie disappeared"));

        ImdbResponseDto response;

        try {
            String uri = "http://www.omdbapi.com/?apikey={key}&t={title}";

            response = client
                    .get()
                    .uri(uri, key, movie.getTitle())
                    .retrieve()
                    .body(ImdbResponseDto.class);
        } catch (Exception e) {
            movie.setStatus(Status.FAILED_NO_MOVIE_WITH_THAT_TITLE);
            return;
        }

        if (response == null || "False".equalsIgnoreCase(response.response())) {
            movie.setStatus(Status.FAILED_NO_MOVIE_WITH_THAT_TITLE);
            return;
        }

        try {
            Thread.sleep(10000);
        } catch (Exception ignore) {

        }

        movie.setRating(response.imdbRating());
        movie.setStatus(Status.SUCCESSFUL);
    }
}
