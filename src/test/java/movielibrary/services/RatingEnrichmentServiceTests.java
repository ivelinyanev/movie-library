package movielibrary.services;

import movielibrary.dtos.imdb.ImdbResponseDto;
import movielibrary.enums.Status;
import movielibrary.models.Movie;
import movielibrary.repositories.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Field;
import java.time.Year;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RatingEnrichmentServiceTests {
    @Mock
    private MovieRepository repository;

    @Mock
    private RestClient mockClient;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Mock
    private RestClient.RequestHeadersUriSpec uriSpec;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Mock
    private RestClient.ResponseSpec responseSpec;

    private RatingEnrichmentService service;

    @BeforeEach
    void setup() throws Exception {
        service = new RatingEnrichmentService(repository);

        // inject the mocked client into the private final field via reflection
        Field clientField = RatingEnrichmentService.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(service, mockClient);

        // set api key field
        Field keyField = RatingEnrichmentService.class.getDeclaredField("key");
        keyField.setAccessible(true);
        keyField.set(service, "TEST_KEY");
    }

    @Test
    void enrichRating_transportException_setsFailed() {
        Movie m = new Movie();
        m.setId(1L);
        m.setTitle("Nope");
        when(repository.findById(1L)).thenReturn(Optional.of(m));

        when(mockClient.get()).thenThrow(new RuntimeException("net"));

        service.enrichRating(1L);

        assertEquals(Status.FAILED_NO_MOVIE_WITH_THAT_TITLE, m.getStatus());
    }

    @Test
    void enrichRating_nullResponse_setsFailed() {
        Movie m = new Movie();
        m.setId(2L);
        m.setTitle("T2");
        when(repository.findById(2L)).thenReturn(Optional.of(m));

        // cast the raw mock to the expected returned type to satisfy Mockito's generics checks
        when(mockClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(anyString(), any(), any())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(ImdbResponseDto.class)).thenReturn(null);

        service.enrichRating(2L);

        assertEquals(Status.FAILED_NO_MOVIE_WITH_THAT_TITLE, m.getStatus());
    }

    @Test
    void enrichRating_responseFalse_setsFailed_withErrorTaken() {
        Movie m = new Movie();
        m.setId(3L);
        m.setTitle("Missing");
        when(repository.findById(3L)).thenReturn(Optional.of(m));

        ImdbResponseDto dto = new ImdbResponseDto(null, "False", "Movie not found!");
        when(mockClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(anyString(), any(), any())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(ImdbResponseDto.class)).thenReturn(dto);

        service.enrichRating(3L);

        assertEquals(Status.FAILED_NO_MOVIE_WITH_THAT_TITLE, m.getStatus());
    }

    @Test
    void enrichRating_success_setsRatingAndSuccessful() {
        Movie m = new Movie();
        m.setId(4L);
        m.setTitle("Exists");
        m.setReleaseYear(Year.of(2010));
        when(repository.findById(4L)).thenReturn(Optional.of(m));

        ImdbResponseDto dto = new ImdbResponseDto(7.4, "True", null);
        when(mockClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(anyString(), any(), any())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(ImdbResponseDto.class)).thenReturn(dto);

        service.enrichRating(4L);

        assertEquals(7.4, m.getRating());
        assertEquals(Status.SUCCESSFUL, m.getStatus());
    }

    @Test
    void enrichRating_imdbRatingNull_butResponseTrue_setsNullRatingAndSuccessful() {
        Movie m = new Movie();
        m.setId(5L);
        m.setTitle("ExistsNoRating");
        when(repository.findById(5L)).thenReturn(Optional.of(m));

        ImdbResponseDto dto = new ImdbResponseDto(null, "True", null);
        when(mockClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(anyString(), any(), any())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(ImdbResponseDto.class)).thenReturn(dto);

        service.enrichRating(5L);

        assertNull(m.getRating());
        assertEquals(Status.SUCCESSFUL, m.getStatus());
    }
}
