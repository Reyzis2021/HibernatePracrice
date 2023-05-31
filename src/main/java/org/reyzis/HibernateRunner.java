package org.reyzis;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.hibernate.Session;
import org.reyzis.dao.CompanyRepository;
import org.reyzis.dao.PaymentRepository;
import org.reyzis.dao.UserRepository;
import org.reyzis.dto.UserCreateDto;
import org.reyzis.entity.Payment;
import org.reyzis.entity.PersonalInfo;
import org.reyzis.entity.Role;
import org.reyzis.entity.User;
import org.reyzis.interceptor.TransactionInterceptor;
import org.reyzis.mapper.CompanyReadMapper;
import org.reyzis.mapper.UserCreateMapper;
import org.reyzis.mapper.UserReadMapper;
import org.reyzis.service.UserService;
import org.reyzis.util.HibernateUtil;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.time.LocalDate;


public class HibernateRunner {

    @Transactional
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        try (var sessionFactory = HibernateUtil.buildSessionFactory()){
            var session = (Session) Proxy.newProxyInstance(sessionFactory.getClass().getClassLoader(), new Class[]{Session.class},
                    ((proxy, method, args1) -> method.invoke(sessionFactory.getCurrentSession(), args1)));

            session.beginTransaction();


            var companyRepository = new CompanyRepository(session);
            var companyReadMapper = new CompanyReadMapper();
            var userReadMapper = new UserReadMapper(companyReadMapper);
            var userCreateMapper = new UserCreateMapper(companyRepository);
            var userRepository = new UserRepository(session);
            var paymentRepository = new PaymentRepository(session);


            var userService = new ByteBuddy().subclass(UserService.class)
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(new TransactionInterceptor(sessionFactory)))
                    .make()
                    .load(UserService.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor(UserRepository.class, UserReadMapper.class, UserCreateMapper.class)
                    .newInstance(userRepository, userReadMapper, userCreateMapper);
            var userCreateDto = new UserCreateDto(
                    PersonalInfo.builder()
                            .firstname("Petr2")
                            .lastname("Petrov")
                            .birthDate(LocalDate.now())
                            .build(),
                    "liza@gmail.com",
                    Role.USER,
                    1
            );

            userService.create(userCreateDto);
            session.getTransaction().commit();


        }
    }


}
