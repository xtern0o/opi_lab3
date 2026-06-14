package org.example.management;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.example.domain.Point;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
@Named("pointsStats")
public class PointsStats extends NotificationBroadcasterSupport implements PointsStatsMBean {
    /**
     * Границы отображаемой области координатной плоскости [-5; 5]
     */
    private static final float DISPLAY_LIMIT = 5f;

    private final AtomicLong totalPoints = new AtomicLong();
    private final AtomicLong missedPoints = new AtomicLong();
    private final AtomicLong notificationSequence = new AtomicLong();

    @Override
    public long getTotalPoints() {
        return totalPoints.get();
    }

    @Override
    public long getMissedPoints() {
        return missedPoints.get();
    }

    @Override
    public void reset() {
        totalPoints.set(0);
        missedPoints.set(0);
    }

    public void addPoint(Point point) {
        totalPoints.incrementAndGet();

        if (!point.isHit()) {
            missedPoints.incrementAndGet();
        }

        if (isOutOfDisplayArea(point)) {
            sendNotification(new Notification(
                    getClass().getName(),
                    this,
                    notificationSequence.incrementAndGet(),
                    System.currentTimeMillis(),
                    String.format("(x=%s, y=%s) out of observable bounds [-5; 5]",
                            point.getX(), point.getY())));
        }
    }

    private boolean isOutOfDisplayArea(Point point) {
        Float x = point.getX();
        Float y = point.getY();
        if (x == null || y == null) {
            return false;
        }
        return Math.abs(x) > DISPLAY_LIMIT || Math.abs(y) > DISPLAY_LIMIT;
    }
}
