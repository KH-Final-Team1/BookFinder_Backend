package com.kh.bookfinder.repository;

import com.kh.bookfinder.entity.EmailAuth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {

  Optional<EmailAuth> findByEmailAndAuthCode(String email, String authCode);
}
