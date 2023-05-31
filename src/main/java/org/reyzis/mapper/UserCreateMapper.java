package org.reyzis.mapper;

import lombok.RequiredArgsConstructor;
import org.reyzis.dao.CompanyRepository;
import org.reyzis.dto.UserCreateDto;
import org.reyzis.entity.User;

@RequiredArgsConstructor
public class UserCreateMapper implements Mapper<UserCreateDto, User> {

    private final CompanyRepository companyRepository;

    @Override
    public User mapFrom(UserCreateDto object) {
        return User.builder()
                .personalInfo(object.personalInfo())
                .username(object.username())
                .role(object.role())
                .company(companyRepository.findById(object.companyId()).orElseThrow(IllegalArgumentException::new))
                .build();
    }
}
