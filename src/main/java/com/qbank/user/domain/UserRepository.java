package com.qbank.user.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
    List<User> findByNicknameContainingIgnoreCaseAndIdNot(String nickname, Long excludeId, Pageable pageable);
}
