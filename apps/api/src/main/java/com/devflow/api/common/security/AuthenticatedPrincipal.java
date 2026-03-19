package com.devflow.api.common.security;

public record AuthenticatedPrincipal(
        Long subjectId,
        PrincipalType principalType,
        String role
) {
}
