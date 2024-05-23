package com.kh.bookfinder.user.service;

import com.kh.bookfinder.auth.login.dto.SecurityUserDetails;
import com.kh.bookfinder.auth.oauth2.dto.OAuth2SignUpDto;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.exception.InvalidFieldException;
import com.kh.bookfinder.user.dto.DuplicateCheckDto;
import com.kh.bookfinder.user.dto.SignUpDto;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import com.kh.bookfinder.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public void createNewUser(SignUpDto signUpDto) {
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

  public User findUser(String email) {
    return this.userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException(Message.NOT_FOUND_USER));
  }

  @Transactional
  public void updateSocialGuestToUser(SecurityUserDetails securityUserDetails, OAuth2SignUpDto signUpDto) {
    String email = securityUserDetails.getUsername();
    User socialGuest = userRepository.findByEmail(email).orElseThrow(
        () -> new ResourceNotFoundException(Message.NOT_FOUND_USER)
    );
    socialGuest.setNickname(signUpDto.getNickname());
    socialGuest.setAddress(signUpDto.getAddress());
    socialGuest.setPhone(signUpDto.getPhone());
    socialGuest.setRole(UserRole.ROLE_USER);
  }
}
