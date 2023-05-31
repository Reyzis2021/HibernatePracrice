package org.reyzis.dao;

import org.hibernate.SessionFactory;
import org.reyzis.entity.Company;

import javax.persistence.EntityManager;


public class CompanyRepository extends RepositoryBase<Integer, Company> {

    public CompanyRepository(EntityManager entityManager) {
        super(Company.class, entityManager);
    }
}

