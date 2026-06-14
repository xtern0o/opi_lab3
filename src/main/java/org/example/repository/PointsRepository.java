package org.example.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.domain.Point;
import org.example.entity.PointEntity;
import org.example.mapper.PointMapper;
import org.example.exception.ValidationException;
import org.example.validator.PointValidator;

import java.util.List;

@ApplicationScoped
public class PointsRepository implements PointsPersistence {
    @Inject
    PointValidator pointValidator;
    @Inject
    PointMapper pointMapper;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void save(Point p) throws ValidationException {
        if (!pointValidator.validate(p)) throw new ValidationException(p);
        em.persist(pointMapper.toEntity(p));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        PointEntity p = em.find(PointEntity.class, id);
        if (p != null) em.remove(p);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Point> getAllCreatedAtDesc() {
        return em.createQuery(
                "SELECT p from PointEntity p ORDER BY p.createdAt DESC",
                PointEntity.class
        ).getResultStream().map(pointMapper::toDomain).toList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Point> getAllFilterByTemperature(Float temperature) {
        return em.createQuery(
                "SELECT p from PointEntity p WHERE p.temperature = :temperature ORDER BY p.createdAt DESC",
                PointEntity.class
        ).setParameter("temperature", temperature).getResultStream().map(pointMapper::toDomain).toList();
    }
}
