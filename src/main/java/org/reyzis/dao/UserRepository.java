package org.reyzis.dao;

import lombok.Getter;
import lombok.Setter;
import org.reyzis.entity.User;

import javax.persistence.EntityManager;


public class UserRepository extends RepositoryBase<Long, User> {

    public UserRepository(EntityManager entityManager) {
        super(User.class, entityManager);
    }
}
