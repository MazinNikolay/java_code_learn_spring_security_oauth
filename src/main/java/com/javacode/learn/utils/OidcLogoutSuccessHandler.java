package com.javacode.learn.utils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OidcLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(OidcLogoutSuccessHandler.class);
    private final ClientRegistrationRepository clientRegistrationRepository;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        logger.info("Handling logout success...");

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            ClientRegistration clientRegistration = clientRegistrationRepository
                    .findByRegistrationId(oauthToken.getAuthorizedClientRegistrationId());
            if (clientRegistration != null) {
                Map<String, Object> configurationMetadata = clientRegistration.getProviderDetails()
                        .getConfigurationMetadata();

                // Проверяем, поддерживает ли провайдер OIDC и имеет ли end_session_endpoint. Это для гугла
                if (configurationMetadata.containsKey("end_session_endpoint")) {
                    String logoutUrl = clientRegistration.getProviderDetails().getConfigurationMetadata()
                            .get("end_session_endpoint").toString();
                    String redirectUri = UriComponentsBuilder.fromHttpUrl(request.getRequestURL()
                                    .toString())
                            .replacePath("/home")
                            .build()
                            .toUriString();

                    String fullLogoutUrl = UriComponentsBuilder.fromHttpUrl(logoutUrl)
                            .queryParam("post_logout_redirect_uri", redirectUri)
                            .build()
                            .toUriString();
                    response.sendRedirect(fullLogoutUrl);
                    return;
                }
            }
        }
        response.sendRedirect("/home");
        super.onLogoutSuccess(request, response, authentication);
    }
}
