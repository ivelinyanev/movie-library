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

import static movielibrary.utils.StringConstants.MOVIE_DISAPPEARED;

@Service
public class RatingEnrichmentService {

    @Value("${omdb.api.key}")
    private String key;

    private final RestClient client;
    private final MovieRepository repository;

    @Autowired
    public RatingEnrichmentService(MovieRepository repository) {
        this.client = RestClient.create();
        this.repository = repository;
    }

    @Async
    @Transactional
    public void enrichRating(Long movieId) {
        Movie movie = repository.findById(movieId)
                .orElseThrow(() -> new IllegalStateException(MOVIE_DISAPPEARED));

        ImdbResponseDto response;

        try {
            String uri = "http://www.omdbapi.com/?apikey={key}&t={title}";

            response = client
                    .get()
                    .uri(uri, key, movie.getTitle())
                    .retrieve()
                    .body(ImdbResponseDto.class);
        } catch (Exception e) {
            movie.setStatus(Status.FAILED_OMDB_API_SERVER_ERROR);
            return;
        }

        if (response == null || "False".equalsIgnoreCase(response.response())) {
            movie.setStatus(Status.FAILED_NO_MOVIE_WITH_THAT_TITLE);
            return;
        }

        movie.setRating(response.imdbRating());
        movie.setStatus(Status.SUCCESSFUL);
    }
}
