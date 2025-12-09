package movielibrary.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;

@Entity
@Table(name = "books")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    Long id;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "director", nullable = false)
    String director;

    @Column(name = "year", nullable = false)
    Year releaseYear;

    @Column(name = "rating")
    Double rating;
}
