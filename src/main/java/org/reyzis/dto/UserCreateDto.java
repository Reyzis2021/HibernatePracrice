package org.reyzis.dto;

import org.reyzis.entity.PersonalInfo;
import org.reyzis.entity.Role;

public record UserCreateDto(PersonalInfo personalInfo,
                            String username,
                            Role role,
                            Integer companyId) {
}
