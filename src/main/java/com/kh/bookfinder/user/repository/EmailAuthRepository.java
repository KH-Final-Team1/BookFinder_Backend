package com.kh.bookfinder.user.repository;

import com.kh.bookfinder.user.entity.EmailAuth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {

  Optional<EmailAuth> findByEmailAndAuthCode(String email, String authCode);
}
