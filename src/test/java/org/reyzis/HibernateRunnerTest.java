package org.reyzis;

import lombok.Cleanup;
import org.hibernate.FlushMode;
import org.hibernate.annotations.QueryHints;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.reyzis.entity.*;
import org.reyzis.util.HibernateUtil;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class HibernateRunnerTest {

    @Test
    void checkHql() {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            var result = session
                    .createQuery("SELECT u from User u " +
                            "join u.company c " +
                            "where u.personalInfo.firstname = :firstname and c.name = :companyName", User.class)
                    .setParameter("firstname", "Ivan")
                    .setParameter("companyName", "Google")
                    .setFlushMode(FlushMode.COMMIT)
                    .setHint(QueryHints.FETCH_SIZE, "50")
                    .list();

            var countRows = session.createQuery("update User u set u.role = 'ADMIN'");
            System.out.println(result);
            session.getTransaction().commit();
        }
    }


    @Test
    void localeInfo() {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            var company = session.get(Company.class, 2L);
            company.getUsers().forEach((k, v) -> System.out.println(v));
            session.getTransaction().commit();
        }
    }

    @Test
    void checkManyToMany() {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

            var user = session.get(User.class, 2L);
            var chat = session.get(Chat.class, 2L);

//            var userChat = UserChat.builder()
//                    .createdAt(Instant.now())
//                    .createdBy(user.getUsername())
//                    .build();
//
//            userChat.setUser(user);
//            userChat.setChat(chat);

            //session.save(userChat);
//            var chat = Chat.builder()
//                    .name("reyzis_chat")
//                    .build();
//
//            user.addChat(chat);
//            session.save(chat);


            session.getTransaction().commit();
        }
    }
   @Test
    void checkOneToOne() {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
//
//            var user = User.builder()
//                    .username("test2aw@gmail.com")
//                    .build();
            var profile = Profile.builder()
                    .language("ru")
                    .street("Kolasa 18")
                    .build();

//            profile.setUser(user);
//            session.save(user);

            session.getTransaction().commit();
        }
    }

    @Test
    void checkLazyInitializationException() {
        Company company = null;
       try( var sessionFactory = HibernateUtil.buildSessionFactory();
         var session = sessionFactory.openSession()) {

           session.beginTransaction();
           company = session.get(Company.class, 4L);
           session.getTransaction().commit();
       }
        //Set<User> users = company.getUsers();
      //  System.out.println(users.size());
    }

    @Test
    void deleteCompany() {
        @Cleanup var sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();

        session.beginTransaction();
        Company company = session.get(Company.class, 3L);
        session.delete(company);
        session.getTransaction().commit();
    }

    @Test
    void addUserToCompany() {
        @Cleanup var sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();

        session.beginTransaction();
        var company = Company.builder()
                .name("Facebook")
                .build();
//        var user = User.builder()
//                .username("arte12sm@gmail.com")
//                .build();
//        company.addUser(user);

        session.save(company);
        session.getTransaction().commit();
    }

    @Test
    void oneToMany() {
        @Cleanup var sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();

            session.beginTransaction();
        Company company = session.get(Company.class, 2L);
        System.out.println(company.getUsers());
            session.getTransaction().commit();
        }


    @Test
    void checkGetReflectionApi() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.getString("username");
        resultSet.getString("lastname");
        resultSet.getString("firstname");

        Class<User> clazz = User.class;

        Constructor<User> constructor = clazz.getConstructor();
        User user = constructor.newInstance();
        Field usernameField = clazz.getDeclaredField("username");
        usernameField.setAccessible(true);
        usernameField.set(user, resultSet.getString("username"));
    }

    @Test
    void checkReflectionApi() throws SQLException, IllegalAccessException {
        User user = null;
        String sql = """
                insert
                into
                %s
                (%s)
                values
                (%s)
                """;
        String tableName = Optional.ofNullable(user.getClass().getAnnotation(Table.class))
                .map(tableAnnotation -> tableAnnotation.schema() + "."
                        + tableAnnotation.name())
                .orElse(user.getClass().getName());


        Field[] declaredFields = user.getClass().getDeclaredFields();

        String columnNames = Arrays.stream(declaredFields)
                .map(field -> Optional.ofNullable(field.getAnnotation(Column.class))
                        .map(Column::name)
                        .orElse(field.getName()))
                .collect(Collectors.joining(", "));

        String columnValues = Arrays.stream(declaredFields)
                .map(declaredField -> "?")
                .collect(Collectors.joining(", "));

        System.out.println(sql.formatted(tableName, columnNames, columnValues));

        Connection connection = null;
        PreparedStatement preparedStatement = connection.prepareStatement(sql.formatted(tableName, columnNames, columnValues));

        for (Field field : declaredFields) {
            field.setAccessible(true);
            preparedStatement.setObject(1, field.get(user));
        }
    }

}