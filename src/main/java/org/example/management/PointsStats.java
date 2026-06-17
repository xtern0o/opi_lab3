package org.example.management;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.example.domain.Point;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
@Named("pointsStats")
public class PointsStats extends NotificationBroadcasterSupport implements PointsStatsMBean {
    private static final float Y_NOTIFY_THRESHOLD = -4.5f;

    private final AtomicLong totalPoints = new AtomicLong();
    private final AtomicLong missedPoints = new AtomicLong();
    private final AtomicLong notificationSequence = new AtomicLong();
    private final AtomicLong totalClicks = new AtomicLong();

    private volatile ObjectName objectName;

    public void setObjectName(ObjectName objectName) {
        this.objectName = objectName;
    }

    @Override
    public long getTotalPoints() {
        return totalPoints.get();
    }

    @Override
    public long getMissedPoints() {
        return missedPoints.get();
    }

    @Override
    public long getTotalClicks() {
        return totalClicks.get();
    }

    @Override
    public void reset() {
        totalPoints.set(0);
        missedPoints.set(0);
        totalClicks.set(0);
    }

    public void addPoint(Point point) {
        totalPoints.incrementAndGet();

        if (!point.isHit()) {
            missedPoints.incrementAndGet();
        }

        if (isBelowYThreshold(point)) {
            sendNotification(new Notification(
                    getClass().getName(),
                    objectName,
                    notificationSequence.incrementAndGet(),
                    System.currentTimeMillis(),
                    String.format("(x=%s, y=%s) y below threshold %s",
                            point.getX(), point.getY(), Y_NOTIFY_THRESHOLD)));
        }
    }

    public void recordClick() {
        totalClicks.incrementAndGet();
    }

    private boolean isBelowYThreshold(Point point) {
        Float y = point.getY();
        if (y == null) {
            return false;
        }
        return y < Y_NOTIFY_THRESHOLD;
    }
}
