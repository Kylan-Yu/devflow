package com.devflow.api.common.security;

import com.devflow.api.modules.auth.entity.AdminStatus;
import com.devflow.api.modules.auth.repository.AdminUserRepository;
import com.devflow.api.modules.auth.service.JwtService;
import com.devflow.api.modules.user.entity.UserStatus;
import com.devflow.api.modules.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AdminUserRepository adminUserRepository;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserRepository userRepository,
                                   AdminUserRepository adminUserRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveAccessToken(request);
        if (token != null) {
            try {
                JwtService.JwtPayload payload = jwtService.parseAccessToken(token);
                if (!isActivePrincipal(payload)) {
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }

                AuthenticatedPrincipal principal = new AuthenticatedPrincipal(
                        payload.subjectId(),
                        payload.principalType(),
                        payload.role()
                );
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + payload.role()))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException ignored) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        if (!"/ws/notifications".equals(request.getRequestURI())) {
            return null;
        }

        String token = request.getParameter("token");
        if (!StringUtils.hasText(token)) {
            return null;
        }
        return token;
    }

    private boolean isActivePrincipal(JwtService.JwtPayload payload) {
        if (payload.principalType() == PrincipalType.USER) {
            return userRepository.findByIdAndStatus(payload.subjectId(), UserStatus.ACTIVE).isPresent();
        }
        if (payload.principalType() == PrincipalType.ADMIN) {
            return adminUserRepository.findByIdAndStatus(payload.subjectId(), AdminStatus.ACTIVE).isPresent();
        }
        return false;
    }
}
