package com.qbank.user.domain;

import com.qbank.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    // OAuth 로그인 사용자는 null
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private int avatarId;

    @Column(nullable = false)
    private String provider;

    @Column
    private String providerId;

    public static User createOAuth(String provider, String providerId, String email, String nickname) {
        User user = new User();
        user.provider = provider.toUpperCase();
        user.providerId = providerId;
        // GitHub는 이메일이 null일 수 있으므로 고유한 플레이스홀더 사용
        user.email = (email != null && !email.isBlank())
                ? email
                : provider.toLowerCase() + "_" + providerId + "@oauth.local";
        user.nickname = (nickname != null && !nickname.isBlank()) ? nickname : "사용자";
        user.avatarId = 0;
        return user;
    }

    public void updateProfile(String nickname, int avatarId) {
        this.nickname = nickname;
        this.avatarId = avatarId;
    }
}
