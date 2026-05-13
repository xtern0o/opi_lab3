package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.dto.PointDto;

import java.time.Instant;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name="points")
public class PointEntity {

    public PointEntity(PointDto pointDTO) {
        this.x = pointDTO.getX();
        this.y = pointDTO.getY();
        this.r = pointDTO.getR();
        this.hit = pointDTO.isHit();
        this.temperature = pointDTO.getTemperature();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Float x;

    @Column(nullable = false)
    private Float y;

    @Column(nullable = false)
    private Float r;

    @Column(nullable = false)
    private Boolean hit;

    @Column(name="created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column // nullable
    private Float temperature;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }

}
