package org.example.management;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardEmitterMBean;
import javax.management.StandardMBean;
import java.lang.management.ManagementFactory;

@ApplicationScoped
public class MBeanRegister {

    private static final String STATS_NAME = "org.example:type=PointsStats";
    private static final String RATIO_NAME = "org.example:type=PointsRatio";

    @Inject
    private PointsStats pointsStats;

    @Inject
    private PointsRatio pointsRatio;

    private final MBeanServer server = ManagementFactory.getPlatformMBeanServer();

    public void register(@Observes @Initialized(ApplicationScoped.class) Object ignored) {
        try {
            ObjectName statsName = new ObjectName(STATS_NAME);
            ObjectName ratioName = new ObjectName(RATIO_NAME);
            if (!server.isRegistered(statsName)) {
                // для исправления бага прокси
                server.registerMBean(new StandardEmitterMBean(pointsStats, PointsStatsMBean.class, pointsStats), statsName);
            }
            if (!server.isRegistered(ratioName)) {
                server.registerMBean(new StandardMBean(pointsRatio, PointsRatioMBean.class), ratioName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to register MBeans", e);
        }
    }

    @PreDestroy
    public void unregister() {
        try {
            ObjectName statsName = new ObjectName(STATS_NAME);
            ObjectName ratioName = new ObjectName(RATIO_NAME);
            if (server.isRegistered(statsName)) {
                server.unregisterMBean(statsName);
            }
            if (server.isRegistered(ratioName)) {
                server.unregisterMBean(ratioName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to unregister MBeans", e);
        }
    }
}
