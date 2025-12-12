package movielibrary.services;

import movielibrary.dtos.imdb.ImdbResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CompletableFuture;

@Service
public class RatingEnrichmentService {

    @Value("${omdb.api.key}")
    private String key;

    private final RestClient client;

    public RatingEnrichmentService() {
        this.client = RestClient.create();
    }

    @Async
    public CompletableFuture<Double> getRating(String title) {
        var response = client
                .get()
                .uri("http://www.omdbapi.com/?apikey={key}&t={title}", key, title)
                .retrieve()
                .body(ImdbResponseDto.class);

        return CompletableFuture.completedFuture(response != null ? response.imdbRating() : null);
    }
}
