package movielibrary.services;

import movielibrary.enums.Status;
import movielibrary.exceptions.DuplicateEntityException;
import movielibrary.exceptions.EntityNotFoundException;
import movielibrary.models.Movie;
import movielibrary.repositories.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceImplTests {
    @Mock
    private MovieRepository movieRepository;

    @Mock
    private RatingEnrichmentServiceImpl ratingEnrichmentService;

    @InjectMocks
    private MovieServiceImpl service;

    @Test
    void getAll_returnsAll() {
        List<Movie> expected = List.of(new Movie(), new Movie());
        when(movieRepository.findAll()).thenReturn(expected);

        List<Movie> actual = service.getAll();

        assertSame(expected, actual);
        verify(movieRepository).findAll();
    }

    @Test
    void getById_found() {
        Movie m = new Movie(); m.setId(1L);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(m));

        Movie actual = service.getById(1L);

        assertSame(m, actual);
    }

    @Test
    void getById_notFound_throws() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void getByTitle_found() {
        Movie m = new Movie(); m.setTitle("T");
        when(movieRepository.findByTitle("T")).thenReturn(Optional.of(m));

        Movie actual = service.getByTitle("T");

        assertSame(m, actual);
    }

    @Test
    void getByTitle_notFound_throws() {
        when(movieRepository.findByTitle("X")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getByTitle("X"));
    }

    @Test
    void create_duplicate_throws() {
        Movie m = new Movie(); m.setTitle("dup");
        when(movieRepository.existsByTitle("dup")).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> service.create(m));
        verify(movieRepository, never()).save(any());
    }

    @Test
    void create_success_savesAndEnriches() {
        Movie m = new Movie();
        m.setTitle("new");
        when(movieRepository.existsByTitle("new")).thenReturn(false);
        when(movieRepository.save(m)).thenReturn(m);

        Movie result = service.create(m);

        assertSame(m, result);
        verify(movieRepository).save(m);
        verify(ratingEnrichmentService).enrichRating(m.getId());
        assertEquals(Status.PENDING, m.getStatus());
    }

    @Test
    void update_notFound_throws() {
        Movie update = new Movie();
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.update(1L, update));
    }

    @Test
    void update_duplicateTitle_throws() {
        Movie existing = new Movie(); existing.setId(1L); existing.setTitle("orig");
        when(movieRepository.findById(1L)).thenReturn(Optional.of(existing));
        // simulate duplicate check
        when(movieRepository.existsByTitleAndIdNot("dup", 1L)).thenReturn(true);

        Movie incoming = new Movie(); incoming.setTitle("dup");
        assertThrows(DuplicateEntityException.class, () -> service.update(1L, incoming));
    }

    @Test
    void update_success_appliesChangesAndSaves() {
        Movie existing = new Movie();
        existing.setId(1L);
        existing.setTitle("old");
        existing.setDirector("oldD");
        existing.setReleaseYear(Year.of(2000));
        existing.setRating(5.0);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(movieRepository.existsByTitleAndIdNot(anyString(), anyLong())).thenReturn(false);

        Movie incoming = new Movie();
        incoming.setTitle("newT");
        incoming.setDirector("newD");
        incoming.setReleaseYear(Year.of(2020));
        incoming.setRating(8.1);

        // service calls saveAndFlush(movie) with the passed "movie" param (per current implementation)
        when(movieRepository.saveAndFlush(incoming)).thenReturn(incoming);

        Movie result = service.update(1L, incoming);

        assertSame(incoming, result);
        verify(movieRepository).saveAndFlush(incoming);
    }

    @Test
    void delete_notFound_throws() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    void delete_found_deletes() {
        Movie m = new Movie(); m.setId(2L);
        when(movieRepository.findById(2L)).thenReturn(Optional.of(m));

        service.delete(2L);

        verify(movieRepository).delete(m);
    }
}
