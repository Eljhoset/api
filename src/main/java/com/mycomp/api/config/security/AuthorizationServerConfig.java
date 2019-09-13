package com.mycomp.api.config.security;

import com.mycomp.api.config.TenantContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpointAuthenticationFilter;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

/**
 *
 * @author Daniel
 */
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    private OAuth2RequestFactory oAuth2RequestFactory;
    @Autowired
    private DataSource dataSource;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .tokenServices(tokenServices())
                .tokenStore(tokenStore());
        this.oAuth2RequestFactory = endpoints.getOAuth2RequestFactory();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);

    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer
                .tokenKeyAccess("hasAuthority('ROLE_TRUSTED_CLIENT')")
                .checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
    }

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setClientDetailsService(clientDetailsService);
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setTokenEnhancer(tokenEnhancer());
        return defaultTokenServices;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new TenantAwareAccessTokenEnhancer();
    }

    private static class TenantAwareAccessTokenEnhancer implements TokenEnhancer {

        @Override
        public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
            final Map<String, Object> additionalInfo = new HashMap<>();
            additionalInfo.put("tenant", TenantContext.getCurrentTenant());
            if (authentication == null || authentication.getUserAuthentication() == null) {
                return accessToken;
            }
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            return accessToken;
        }
    }

    @Bean
    public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver(ApplicationContext applicationContext) {
        final ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new MappingJackson2HttpMessageConverter(Jackson2ObjectMapperBuilder.json().applicationContext(applicationContext).build()));
        resolver.setMessageConverters(messageConverters);
        resolver.setApplicationContext(applicationContext);
        resolver.afterPropertiesSet();
        return resolver;
    }

    @Bean
    public AccessTokenConverter accessTokenConverter() {
        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        return accessTokenConverter;
    }

    @Configuration
    protected class AuthorizationServerSecurityConfig extends AuthorizationServerSecurityConfiguration {

        @Autowired
        private ExceptionHandlerExceptionResolver resolver;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            super.configure(auth);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
            final TokenEndpointAuthenticationFilter tokenEndpointAuthenticationFilter = new TokenEndpointAuthenticationFilter(authenticationManager, oAuth2RequestFactory);
            tokenEndpointAuthenticationFilter.setAuthenticationEntryPoint((HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) -> {
                resolver.resolveException(request, response, null, authException);
            });
            http.addFilterAfter(tokenEndpointAuthenticationFilter, BasicAuthenticationFilter.class);
        }
    }

}
