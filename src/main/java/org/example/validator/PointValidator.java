package org.example.validator;

import jakarta.enterprise.context.ApplicationScoped;
import org.example.domain.Point;

@ApplicationScoped
public class PointValidator extends AbstractValidator<Point> {
    public PointValidator() {
        super(point -> (
                (1 <= point.getR() && point.getR() <= 5) &&
                (-4 <= point.getX() && point.getX() <= 4) &&
                (-5 < point.getY() && point.getY() < 3)
            )
        );
    }

    public boolean checkArea(Point point) {
        Float x = point.getX(), y = point.getY(), r = point.getR();

        if (x >= 0 && y >= 0) {
            return x * x + y * y < (r / 2) * (r / 2);
        }
        else if (x < 0 && y >= 0) {
            return y <= x / 2 + r / 2;
        }
        else if (x < 0 && y < 0) {
            return false;
        }
        else {
            return x <= r / 2 && y >= -r;
        }
    }
}
