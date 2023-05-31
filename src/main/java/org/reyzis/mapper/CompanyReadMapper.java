package org.reyzis.mapper;

import org.reyzis.dto.CompanyReadDto;
import org.reyzis.entity.Company;

public class CompanyReadMapper implements Mapper<Company, CompanyReadDto> {

    @Override
    public CompanyReadDto mapFrom(Company object) {
        return new CompanyReadDto(
                object.getId(),
                object.getName(),
                object.getLocales()
        );
    }
}
