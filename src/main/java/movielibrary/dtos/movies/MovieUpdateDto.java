package movielibrary.dtos.movies;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.time.Year;

import static movielibrary.utils.StringConstants.YEAR_LIMITATION;

public record MovieUpdateDto(

        String title,

        String director,

        Year releaseYear,

        @DecimalMin(value = "0.0")
        @DecimalMax(value = "10.0")
        Double rating
) {

    @AssertTrue(message = YEAR_LIMITATION)
    public boolean isReleaseYearValid() {
        if (releaseYear == null) return true; // null value allowed for updates
        int year = releaseYear.getValue();
        int max = Year.now().getValue() + 1;
        return year >= 1888 && year <= max;
    }

}
