package org.reyzis.mapper;

import lombok.AllArgsConstructor;
import org.reyzis.dto.UserReadDto;
import org.reyzis.entity.User;

import java.util.Optional;

@AllArgsConstructor
public class UserReadMapper implements Mapper <User, UserReadDto>{

    private final CompanyReadMapper companyReadMapper;

    @Override
    public UserReadDto mapFrom(User object) {
        return new UserReadDto(
                object.getId(),
                object.getPersonalInfo(),
                object.getUsername(),
                object.getRole(),
                Optional.ofNullable(object.getCompany()).map(companyReadMapper::mapFrom)
                        .orElse(null));

    }
}
