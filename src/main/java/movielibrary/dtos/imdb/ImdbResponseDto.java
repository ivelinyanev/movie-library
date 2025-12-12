package movielibrary.dtos.imdb;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ImdbResponseDto(
        @JsonProperty("imdbRating")
        Double imdbRating,

        @JsonProperty("Response")
        String response,

        @JsonProperty("Error")
        String error
) {
}
