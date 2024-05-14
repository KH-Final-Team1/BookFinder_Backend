package com.kh.bookfinder.user.service;

import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.exception.InvalidFieldException;
import com.kh.bookfinder.user.dto.DuplicateCheckDto;
import com.kh.bookfinder.user.dto.SignUpDto;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public void save(SignUpDto signUpDto) {
    if (!signUpDto.equalsPassword()) {
      throw new InvalidFieldException("password", Message.INVALID_PASSWORD_CONFIRM);
    }
    this.userRepository
        .findByEmail(signUpDto.getEmail())
        .ifPresent((value) -> {
          throw new InvalidFieldException("email", Message.DUPLICATE_EMAIL);
        });
    this.userRepository
        .findByNickname(signUpDto.getNickname())
        .ifPresent((value) -> {
          throw new InvalidFieldException("nickname", Message.DUPLICATE_NICKNAME);
        });

    User user = signUpDto.toEntity(passwordEncoder);
    this.userRepository.save(user);
  }

  public void checkDuplicate(DuplicateCheckDto duplicateCheckDto) {
    if (duplicateCheckDto.getField().equals("email")) {
      this.userRepository.findByEmail(duplicateCheckDto.getValue())
          .ifPresent((value) -> {
            throw new InvalidFieldException("email", Message.DUPLICATE_EMAIL);
          });
    }
    if (duplicateCheckDto.getField().equals("nickname")) {
      this.userRepository.findByNickname(duplicateCheckDto.getValue())
          .ifPresent((value) -> {
            throw new InvalidFieldException("nickname", Message.DUPLICATE_NICKNAME);
          });
    }

  }
}
