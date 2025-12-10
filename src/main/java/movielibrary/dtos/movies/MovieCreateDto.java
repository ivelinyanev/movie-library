package movielibrary.dtos.movies;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Year;

import static movielibrary.utils.StringConstants.YEAR_LIMITATION;

public record MovieCreateDto(

        @NotBlank
        String title,

        @NotBlank
        String director,

        @NotNull
        Year releaseYear
) {

    @AssertTrue(message = YEAR_LIMITATION)
    public boolean isReleaseYearValid() {
        if (releaseYear == null) return false;
        int year = releaseYear.getValue();
        int max = Year.now().getValue() + 1;
        return year >= 1888 && year <= max;
    }
}
