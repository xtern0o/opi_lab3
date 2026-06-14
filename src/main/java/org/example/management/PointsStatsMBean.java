package org.example.management;

public interface PointsStatsMBean {
    long getTotalPoints();
    long getMissedPoints();
    void reset();
    long getTotalClicks();
}
