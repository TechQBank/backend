package com.qbank.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private JwtProvider jwtProvider;
    private String secret;

    @BeforeEach
    void setup() {
        // 33 bytes = 264 bits (HMAC-SHA256 requires minimum 256 bits)
        secret = Base64.getEncoder().encodeToString(
                "test-secret-for-unit-testing-1234".getBytes()
        );
        jwtProvider = new JwtProvider(secret, 3_600_000L);
    }

    @Test
    void 토큰_생성_후_userId_정확히_추출() {
        String token = jwtProvider.generate(42L);

        assertThat(jwtProvider.extractUserId(token)).isEqualTo(42L);
    }

    @Test
    void 유효한_토큰은_isValid_true() {
        String token = jwtProvider.generate(1L);

        assertThat(jwtProvider.isValid(token)).isTrue();
    }

    @Test
    void 만료된_토큰은_isValid_false() {
        JwtProvider expiredProvider = new JwtProvider(secret, -1L); // 즉시 만료
        String token = expiredProvider.generate(1L);

        assertThat(jwtProvider.isValid(token)).isFalse();
    }

    @Test
    void 시그니처가_변조된_토큰은_isValid_false() {
        String token = jwtProvider.generate(1L);

        assertThat(jwtProvider.isValid(token + "tampered")).isFalse();
    }

    @Test
    void 다른_시크릿으로_서명된_토큰은_isValid_false() {
        String otherSecret = Base64.getEncoder().encodeToString(
                "other-secret-key-for-testing-99999".getBytes()
        );
        JwtProvider otherProvider = new JwtProvider(otherSecret, 3_600_000L);
        String token = otherProvider.generate(1L);

        assertThat(jwtProvider.isValid(token)).isFalse();
    }

    @Test
    void 빈_문자열은_isValid_false() {
        assertThat(jwtProvider.isValid("")).isFalse();
    }

    @Test
    void JWT_형식이_아닌_문자열은_isValid_false() {
        assertThat(jwtProvider.isValid("not.a.jwt")).isFalse();
    }
}
