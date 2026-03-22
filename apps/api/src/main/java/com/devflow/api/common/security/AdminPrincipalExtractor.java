package com.devflow.api.common.security;

import com.devflow.api.common.api.ResponseCode;
import com.devflow.api.common.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AdminPrincipalExtractor {

    public Long requireAdminId(Authentication authentication) {
        if (authentication == null) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED);
        }

        Object principalObject = authentication.getPrincipal();
        if (!(principalObject instanceof AuthenticatedPrincipal)) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED);
        }

        AuthenticatedPrincipal principal = (AuthenticatedPrincipal) principalObject;
        if (principal.principalType() != PrincipalType.ADMIN) {
            throw new BusinessException(ResponseCode.FORBIDDEN);
        }

        return principal.subjectId();
    }
}
