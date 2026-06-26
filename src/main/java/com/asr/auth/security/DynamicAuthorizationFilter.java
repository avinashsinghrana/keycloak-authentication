package com.asr.auth.security;

import com.asr.auth.domain.entity.RoleApiMapping;
import com.asr.auth.service.RoleApiService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicAuthorizationFilter extends OncePerRequestFilter {

    private final RoleApiService roleApiService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        // Skip permitAll paths (you can also configure this centrally or rely on SecurityConfig to bypass this filter)
        if (requestUri.startsWith("/api/v1/auth/") || requestUri.startsWith("/swagger-ui") || requestUri.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> userRoles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toList());

        boolean isAuthorized = false;

        for (String role : userRoles) {
            List<RoleApiMapping> allowedApis = roleApiService.getRoleApiMappings(role);
            for (RoleApiMapping mapping : allowedApis) {
                if (mapping.getApiMaster().getMethod().equalsIgnoreCase(requestMethod) &&
                        pathMatcher.match(mapping.getApiMaster().getPath(), requestUri)) {
                    isAuthorized = true;
                    break;
                }
            }
            if (isAuthorized) break;
        }

        if (!isAuthorized) {
            log.warn("Access Denied for user roles {} trying to access {} {}", userRoles, requestMethod, requestUri);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
