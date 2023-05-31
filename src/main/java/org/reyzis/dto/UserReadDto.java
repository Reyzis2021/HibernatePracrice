package org.reyzis.dto;

import org.reyzis.entity.PersonalInfo;
import org.reyzis.entity.Role;

public record UserReadDto(Long id,
                          PersonalInfo personalInfo,
                          String username,
                          Role role,
                          CompanyReadDto companyReadDto) {
}
