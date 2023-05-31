package org.reyzis.util;

import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.reyzis.entity.Audit;
import org.reyzis.listener.AuditTableListener;


@UtilityClass
public class HibernateUtil {

    public static SessionFactory buildSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Audit.class);
        configuration.configure();

        var sessionFactory = configuration.buildSessionFactory();
      //  registerListeners(sessionFactory);
        return sessionFactory;
    }

    private static void registerListeners(SessionFactory sessionFactory) {
        var sessionFactoryImpl = sessionFactory.unwrap(SessionFactoryImpl.class);
        var listenerRegistry = sessionFactoryImpl.getServiceRegistry().getService(EventListenerRegistry.class);
        listenerRegistry.appendListeners(EventType.PRE_INSERT, new AuditTableListener());
        listenerRegistry.appendListeners(EventType.PRE_DELETE, new AuditTableListener());
    }
}
