package org.reyzis.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.graph.GraphSemantic;
import org.reyzis.dao.UserRepository;
import org.reyzis.dto.UserCreateDto;
import org.reyzis.mapper.UserCreateMapper;
import org.reyzis.dto.UserReadDto;
import org.reyzis.entity.User;
import org.reyzis.mapper.Mapper;
import org.reyzis.mapper.UserReadMapper;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserReadMapper readMapper;
    private final UserCreateMapper createMapper;

    @Transactional
    public Long create (UserCreateDto dto) {
    // TODO: add validation
     return userRepository.save(createMapper.mapFrom(dto)).getId();
    }

    @Transactional
    public Optional<UserReadDto> findById(Long id) {
       return findById(id, readMapper);
    }

    @Transactional
    public <T> Optional<T> findById(Long id, Mapper<User, T> mapper) {
        Map<String, Object> properties = Map.of(
                GraphSemantic.LOAD.getJpaHintName(),
                userRepository.getEntityManager().getEntityGraph("withCompany"));


        return userRepository.findById(id, properties)
                .map(mapper::mapFrom);
    }

    @Transactional
    public boolean delete(Long id) {

        var maybeUser = userRepository.findById(id);
        maybeUser.ifPresent(user -> userRepository.delete(user.getId()));
        userRepository.delete(id);
        return maybeUser.isPresent();
    }
}
