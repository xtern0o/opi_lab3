package org.example.repository;

import org.example.domain.Point;
import org.example.exception.ValidationException;

import java.util.List;

public interface PointsPersistence {
    void save(Point p) throws ValidationException;
    void deleteById(Long id);
    List<Point> getAllCreatedAtDesc();
    List<Point> getAllFilterByTemperature(Float temperature);
}
