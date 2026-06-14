package org.example.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.example.domain.Point;
import org.example.dto.PointDto;
import org.example.entity.PointEntity;

@ApplicationScoped
public class PointMapper {

    public Point toDomain(PointDto dto) {
        return new Point(dto.getX(), dto.getY(), dto.getR(), dto.getTemperature(), dto.isHit());
    }

    public Point toDomain(PointEntity entity) {
        return new Point(entity.getX(), entity.getY(), entity.getR(), entity.getTemperature(), entity.getHit());
    }

    public PointDto toDto(Point point) {
        return new PointDto(point.getX(), point.getY(), point.getR(), point.getTemperature(), point.isHit());
    }

    public PointEntity toEntity(Point point) {
        PointEntity entity = new PointEntity();
        entity.setX(point.getX());
        entity.setY(point.getY());
        entity.setR(point.getR());
        entity.setTemperature(point.getTemperature());
        entity.setHit(point.isHit());
        return entity;
    }
}
