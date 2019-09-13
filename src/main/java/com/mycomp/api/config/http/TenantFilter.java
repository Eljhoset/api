package com.mycomp.api.config.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mycomp.api.config.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 * @author Daniel
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantFilter implements Filter {

    private static final String TENANT_HEADER = "Tenant";
    private static final String AUTHENTICATION_SCHEME = "Bearer";
    @Autowired
    private ObjectMapper objectMapper;

    @Lazy
    @Autowired
    private ResourceServerTokenServices resourceServerTokenServices;

    @Lazy
    @Autowired
    private AccessTokenConverter accessTokenConverter;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String tenantHeader = getTenantFromAccessToken(req);

        if (!EndpointRequest.to("health").matches(req)) {
            if (tenantHeader == null) {
                tenantHeader = req.getHeader(TENANT_HEADER);
            }
            if (tenantHeader != null) {
                TenantContext.setCurrentTenant(tenantHeader);
            } else {
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.put("error", "No tenant header supplied");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(objectNode.toString());
                response.getWriter().flush();
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }

    private String getTenantFromAccessToken(HttpServletRequest req) {
        String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        // Validate the Authorization header
        if (isTokenBasedAuthentication(authorizationHeader)) {
            // Extract the token from the Authorization header
            String token = authorizationHeader
                    .substring(AUTHENTICATION_SCHEME.length()).trim();

            // Validate token
            OAuth2AccessToken accessToken = resourceServerTokenServices.readAccessToken(token);
            if (accessToken == null) {
                return null;
            }
            OAuth2Authentication authentication = resourceServerTokenServices.loadAuthentication(accessToken.getValue());
            Map<String, Object> checkToken = (Map<String, Object>) accessTokenConverter.convertAccessToken(accessToken, authentication);

            return (String) checkToken.get("tenant");
        }
        return null;
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

}
