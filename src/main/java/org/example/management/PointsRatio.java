package org.example.management;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
@Named("pointsRatio")
public class PointsRatio implements PointsRatioMBean {

    @Inject
    private PointsStats pointsStats;

    @Override
    public double getMissesToClicksPercentRatio() {
        long totalClicks = pointsStats.getTotalClicks();
        return totalClicks == 0 ? 0.0 : (double) pointsStats.getMissedPoints() / totalClicks * 100;
    }
}
