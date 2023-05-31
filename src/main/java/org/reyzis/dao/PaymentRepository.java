package org.reyzis.dao;

import org.hibernate.SessionFactory;
import org.reyzis.entity.Payment;

import javax.persistence.EntityManager;


public class PaymentRepository extends RepositoryBase<Long, Payment> {

    public PaymentRepository(EntityManager entityManager) {
        super(Payment.class, entityManager);
    }
}

