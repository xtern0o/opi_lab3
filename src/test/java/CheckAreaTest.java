import org.example.domain.Point;
import org.example.validator.PointValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class CheckAreaTest {
    private static PointValidator pointValidator;

    @BeforeAll
    public static void initAll() {
        pointValidator = new PointValidator();
    }

    @ParameterizedTest
    @CsvSource({
            "-1.0, 1.0, 5.0, true",
            "-3.0, 2.0, 5.0, false"
    })
    void checkTriangleArea(float x, float y, float r, boolean expected) {
        assertEquals(
                expected,
                pointValidator.checkArea(new Point(x, y, r, 0F, false)),
                String.format(
                        "(%f, %f) should %s be inside of triangle if r == %f",
                        x, y, (expected ? "" : "not"), r

                )
        );
    }

    @ParameterizedTest
    @CsvSource({
            "1.0, 1.0, 5.0, true",
            "3.0, 2.0, 5.0, false"
    })
    void checkCircleArea(float x, float y, float r, boolean expected) {
        assertEquals(
                expected,
                pointValidator.checkArea(new Point(x, y, r, 0F, false)),
                String.format(
                        "(%f, %f) should %s be inside of circle if r == %f",
                        x, y, (expected ? "" : "not"), r
                )
        );    }

    @ParameterizedTest
    @CsvSource({
            "2.0, -1.0, 5.0, true",
            "3.0, 0.0, 5.0, false",
            "1.0, -2.9, 3.0, true",
            "1.0, -3.1, 3.0, false"
    })
    void checkRectangleArea(float x, float y, float r, boolean expected) {
        assertEquals(
                expected,
                pointValidator.checkArea(new Point(x, y, r, 0F, false)),
                String.format(
                        "(%f, %f) should %s be inside of rectangle if r == %f",
                        x, y, (expected ? "" : "not"), r
                )
        );    }

    @ParameterizedTest
    @CsvSource({
            "-1.0, -1.0, 5.0",
            "-3.0, -0.5, 3.0"
    })
    void checkEmptyArea(float x, float y, float r) {
        assertFalse(
                pointValidator.checkArea(new Point(x, y, r, 0F, false)),
                String.format(
                        "(%f, %f) should be a miss with any r (especially r == %f)",
                        x, y, r
                )
        );
    }
}
