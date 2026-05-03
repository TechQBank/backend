package com.qbank.auth.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                         AuthenticationException exception) throws IOException {
        String errorCode = resolveErrorCode(exception);
        log.warn("OAuth2 login failed [{}]: {}", errorCode, exception.getMessage());
        response.sendRedirect(frontendUrl + "/oauth/callback?error=" + errorCode);
    }

    private String resolveErrorCode(AuthenticationException e) {
        // 사용자가 OAuth 동의 화면에서 취소/거부한 경우
        if (e instanceof OAuth2AuthenticationException oauthEx) {
            String code = oauthEx.getError().getErrorCode();
            return "access_denied".equalsIgnoreCase(code) ? "ACCESS_DENIED" : "OAUTH_FAILED";
        }
        // loadUser() 내부(DB 저장 등)에서 예외가 발생한 경우
        if (e instanceof InternalAuthenticationServiceException) {
            return "SERVER_ERROR";
        }
        return "OAUTH_FAILED";
    }
}
