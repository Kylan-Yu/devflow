package com.devflow.api.common.security;

import com.devflow.api.common.api.ResponseCode;
import com.devflow.api.common.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserPrincipalExtractor {

    public Long requireUserId(Authentication authentication) {
        if (authentication == null) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED);
        }

        Object principalObject = authentication.getPrincipal();
        if (!(principalObject instanceof AuthenticatedPrincipal)) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED);
        }

        AuthenticatedPrincipal principal = (AuthenticatedPrincipal) principalObject;
        if (principal.principalType() != PrincipalType.USER) {
            throw new BusinessException(ResponseCode.FORBIDDEN);
        }

        return principal.subjectId();
    }
}
