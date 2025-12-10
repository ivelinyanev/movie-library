package movielibrary.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;

@Entity
@Table(name = "movies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    Long id;

    @Column(name = "title", nullable = false, unique = true)
    String title;

    @Column(name = "director", nullable = false)
    String director;

    @Column(name = "year", nullable = false)
    Year releaseYear;

    @Column(name = "rating")
    Double rating;

    public Movie(String title, String director, Year releaseYear) {
        this.title = title;
        this.director = director;
        this.releaseYear = releaseYear;
    }
}
