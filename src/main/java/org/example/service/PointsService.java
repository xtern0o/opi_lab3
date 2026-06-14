package org.example.service;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.domain.Point;
import org.example.repository.PointsRepository;
import org.example.exception.ValidationException;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class PointsService {
    private final List<Point> pointsCache = new CopyOnWriteArrayList<>();

    @Inject
    PointsRepository pointsRepository;

    @PostConstruct
    void init() {
        refresh();
    }

    public List<Point> getAll() {
        return Collections.unmodifiableList(pointsCache);
    }

    public synchronized void refresh() {
        List<Point> fresh = pointsRepository.getAllCreatedAtDesc();
        pointsCache.clear();
        pointsCache.addAll(fresh);
    }

    public void add(Point p) throws ValidationException {
        pointsRepository.save(p);
        pointsCache.add(p);
    }

    public void addAll(List<Point> points) throws ValidationException {
        points.forEach(this::add);
    }
}
