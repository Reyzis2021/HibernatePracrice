package org.reyzis.dao;

import com.querydsl.jpa.impl.JPAQuery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.reyzis.entity.*;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {

    private static final UserDao INSTANCE = new UserDao();

    /**
     * Возвращает всех сотрудников
     */
    public List<User> findAll(Session session) {
        return session.createQuery("select u from User u",User.class).list();

//        var criteriaBuilder = session.getCriteriaBuilder();
//        var criteria = criteriaBuilder.createQuery(User.class);
//
//        var user = criteria.from(User.class);
//        criteria.select(user);
//
//        return session.createQuery(criteria).list();

//        return new JPAQuery<User>(session)
//                .select(QUser.user)
//                .from(QUser.user)
//                .fetch();
    }

    /**
     * Возвращает всех сотрудников с указанным именем
     */
    public List<User> findAllByFirstName(Session session, String firstName) {
        return session.createQuery("select u from User u where u.personalInfo.firstname = :firstName", User.class)
                .setParameter("firstName", firstName)
                .list();
//
//        var criteriaBuilder = session.getCriteriaBuilder();
//        var criteria = criteriaBuilder.createQuery(User.class);
//
//        var user = criteria.from(User.class);
//        criteria.select(user).where(criteriaBuilder.equal(user.get(User_.personalInfo).get(PersonalInfo_.firstname), firstName));
//
//        return session.createQuery(criteria).list();

//        return new JPAQuery<User>(session)
//                .select(QUser.user)
//                .from(QUser.user)
//                .where(QUser.user.personalInfo.firstname.eq(firstName))
//                .fetch();
    }

    /**
     * Возвращает первые {limit} сотрудников, упорядоченных по дате рождения (в порядке возрастания)
     */
    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
        return session.createQuery("select u from User u order by u.personalInfo.birthDate", User.class)
                .setMaxResults(limit)
                .list();

//        var criteriaBuilder = session.getCriteriaBuilder();
//        var criteria = criteriaBuilder.createQuery(User.class);
//
//        var user = criteria.from(User.class);
//        criteria.select(user).orderBy(criteriaBuilder.asc(user.get(User_.personalInfo).get(PersonalInfo_.birthDate)));

      // return session.createQuery(criteria).list();

//        return new JPAQuery<User>(session)
//                .select(QUser.user)
//                .from(QUser.user)
//                .orderBy(QUser.user.personalInfo.birthDate.asc())
//                .limit(limit)
//                .fetch();
    }

    /**
     * Возвращает всех сотрудников компании с указанным названием
     */
    public List<User> findAllByCompanyName(Session session, String companyName) {

        return session.createQuery("select u from User u " +
                        "join u.company c " +
                        "where c.name = :companyName", User.class)
                .setParameter("companyName", companyName)
                .list();
//        var criteriaBuilder = session.getCriteriaBuilder();
//        var criteria = criteriaBuilder.createQuery(User.class);
//
//        var company  = criteria.from(Company.class);
//        var users = company.join(Company_.users);
//        criteria.select(users).where(criteriaBuilder
//                .equal(company.get(Company_.name), companyName));
//        return session.createQuery(criteria).list();

//        return new JPAQuery<User>(session)
//                .from(QUser.user)
//                .join(QCompany.company)
//                .where(QCompany.company.name.eq(companyName))
//                .fetch();
    }


    /**
     * Возвращает все выплаты, полученные сотрудниками компании с указанными именем,
     * упорядоченные по имени сотрудника, а затем по размеру выплаты
     */
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
        return session.createQuery("select p from Payment p " +
                "join p.receiver u " +
                "join u.company c where c.name =:companyName order by u.personalInfo.firstname, p.amount", Payment.class)
                .setParameter("companyName", companyName)
                .list();
//        var criteriaBuilder = session.getCriteriaBuilder();
//        var criteria = criteriaBuilder.createQuery(Payment.class);
//        var payments = criteria.from(Payment.class);
//        var user = payments.join(Payment_.receiver);
//        var company = user.join(User_.company);
//
//        criteria.select(payments).where(
//                criteriaBuilder.equal(company.get(Company_.name), companyName))
//                .orderBy(
//                        criteriaBuilder.asc(user.get(User_.personalInfo).get(PersonalInfo_.firstname)),
//                        criteriaBuilder.asc(payments.get(Payment_.amount))
//                );
//
//        return session.createQuery(criteria).list();

//        return new JPAQuery<Payment>(session)
//                .select(QPayment.payment)
//                .from(QPayment.payment)
//                .join(QUser.user)
//                .join(QCompany.company)

    }

    /**
     * Возвращает среднюю зарплату сотрудника с указанными именем и фамилией
     */
    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstName, String lastName) {
        return session.createQuery("select avg(p.amount) from Payment p join p.receiver u " +
                "where u.personalInfo.firstname = :firstName and u.personalInfo.lastname =:lastName", Double.class)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .uniqueResult();
//        var criteriaBuilder = session.getCriteriaBuilder();
//        var criteria = criteriaBuilder.createQuery(Double.class);
//        var payment = criteria.from(Payment.class);
//        var user = payment.join(Payment_.receiver);
//
//        List<Predicate> predicates = new ArrayList<>();
//        if (firstName!=null){
//            predicates.add(criteriaBuilder.equal(user.get(User_.personalInfo).get(PersonalInfo_.firstname), firstName));
//        }
//        if (lastName!=null){
//            predicates.add(criteriaBuilder.equal(user.get(User_.personalInfo).get(PersonalInfo_.lastname), firstName));
//        }
//
//        criteria.select(criteriaBuilder.avg(payment.get(Payment_.amount)))
//                .where(predicates.toArray(Predicate[]::new));
//
//
//        return session.createQuery(criteria).uniqueResult();
    }

    /**
     * Возвращает для каждой компании: название, среднюю зарплату всех её сотрудников. Компании упорядочены по названию.
     */
    public List<Object[]> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
        return session.createQuery("select c.name, avg(p.amount) from Payment p " +
                        "join p.receiver u " +
                        "join  u.company c group by c.name order by c.name", Object[].class)
                .list();

//        var criteriaBuilder = session.getCriteriaBuilder();
//        var criteria = criteriaBuilder.createQuery(Object[].class);
//        var payment = criteria.from(Payment.class);
//        var user = payment.join(Payment_.receiver);
//        var company = user.join(User_.company);
//
//        criteria.multiselect(company.get(Company_.name), criteriaBuilder.avg(payment.get(Payment_.amount)))
//                .groupBy(company.get(Company_.name))
//                .orderBy(criteriaBuilder.asc(company.get(Company_.name)));
//
//        return session.createQuery(criteria).list();
    }

    /**
     * Возвращает список: сотрудник (объект User), средний размер выплат, но только для тех сотрудников, чей средний размер выплат
     * больше среднего размера выплат всех сотрудников
     * Упорядочить по имени сотрудника
     */
    public List<Object[]> isItPossible(Session session) {

        return session.createQuery("select u, avg(p.amount) from User u " +
                "join u.payments p " +
                "group by u " +
                "having avg(p.amount) > (select avg(p.amount) from Payment p)" +
                        "order by u.personalInfo.firstname", Object[].class)
                .list();

//        var criteriaBuilder = session.getCriteriaBuilder();
//        var criteria = criteriaBuilder.createQuery(Tuple.class);
//
//        var user = criteria.from(User.class);
//        var payment = user.join(User_.payments);
//
//        var subquery = criteria.subquery(Double.class);
//        var paymentSub = subquery.from(Payment.class);
//
//        criteria.select(criteriaBuilder.tuple(
//                                user,
//                                criteriaBuilder.avg(payment.get(Payment_.amount))
//                        ))
//                .groupBy(user.get(User_.id))
//                .having(criteriaBuilder.gt(criteriaBuilder.avg(payment.get(Payment_.amount)),
//                        subquery.select(criteriaBuilder.avg(paymentSub.get(Payment_.amount)))
//                        ))
//                .orderBy(criteriaBuilder.asc(user.get(User_.personalInfo).get(PersonalInfo_.firstname)));
//
//        return session.createQuery(criteria).list();
    }

    public static UserDao getInstance() {
        return INSTANCE;
    }
}