package org.example.management;

public interface PointsStatsMBean {

    /**
     * @return общее число установленных пользователем точек
     */
    long getTotalPoints();

    /**
     * @return число точек, не попавших в область (промахи)
     */
    long getMissedPoints();

    /**
     * Сбросить всю накопленную статистику в ноль.
     */
    void reset();
}
