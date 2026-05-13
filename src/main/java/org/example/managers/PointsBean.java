package org.example.managers;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.entity.PointEntity;
import org.example.repository.PointsRepository;
import org.example.utils.exceptions.ValidationError;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Managed Bean для управления точками
 */
@ApplicationScoped
public class PointsBean {
    private final List<PointEntity> pointsCache = new CopyOnWriteArrayList<>();

    @Inject
    PointsRepository pointsRepository;

    @PostConstruct
    void init() {
        refresh();
    }

    public List<PointEntity> getAll() {
        return Collections.unmodifiableList(pointsCache);
    }

    public synchronized void refresh() {
        List<PointEntity> fresh = pointsRepository.getAllCreatedAtDesc();
        pointsCache.clear();
        pointsCache.addAll(fresh);
    }

    public void add(PointEntity p) throws ValidationError {
        pointsRepository.save(p);
        pointsCache.add(p);
    }

    public void addAll(List<PointEntity> points) throws ValidationError {
        points.forEach(this::add);
    }

    public String testString() {
        return "Hello, pointsBean!";
    }
}
