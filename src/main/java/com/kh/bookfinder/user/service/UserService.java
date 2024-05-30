package com.kh.bookfinder.user.service;

import com.kh.bookfinder.auth.login.dto.SecurityUserDetails;
import com.kh.bookfinder.auth.oauth2.dto.OAuth2SignUpDto;
import com.kh.bookfinder.book_trade.repository.BookTradeRepository;
import com.kh.bookfinder.borough.entity.Borough;
import com.kh.bookfinder.borough.repository.BoroughRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.exception.DuplicateResourceException;
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
  private final BoroughRepository boroughRepository;
  private final BookTradeRepository bookTradeRepository;
  private final PasswordEncoder passwordEncoder;

  public void createNewUser(SignUpDto signUpDto) {
    if (alreadyExistEmail(signUpDto.getEmail())) {
      throw new DuplicateResourceException(Message.DUPLICATE_EMAIL);
    }
    if (alreadyExistNickname(signUpDto.getNickname())) {
      throw new DuplicateResourceException(Message.DUPLICATE_NICKNAME);
    }

    User user = signUpDto.toEntity(passwordEncoder);
    Borough borough = this.boroughRepository
        .findByName(user.extractBoroughName())
        .orElseThrow(() -> new InvalidFieldException("address", Message.INVALID_ADDRESS));
    user.setBorough(borough);
    this.userRepository.save(user);
  }

  public void checkDuplicate(DuplicateCheckDto duplicateCheckDto) {
    if (duplicateCheckDto.getField().equals("email") && alreadyExistEmail(duplicateCheckDto.getValue())) {
      throw new DuplicateResourceException(Message.DUPLICATE_EMAIL);
    }
    if (duplicateCheckDto.getField().equals("nickname") && alreadyExistNickname(duplicateCheckDto.getValue())) {
      throw new DuplicateResourceException(Message.DUPLICATE_NICKNAME);
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
    Borough borough = this.boroughRepository
        .findByName(socialGuest.extractBoroughName())
        .orElseThrow(() -> new InvalidFieldException("address", Message.INVALID_ADDRESS));

    socialGuest.setNickname(signUpDto.getNickname());
    socialGuest.setAddress(signUpDto.getAddress());
    socialGuest.setPhone(signUpDto.getPhone());
    socialGuest.setRole(UserRole.ROLE_USER);
    socialGuest.setBorough(borough);
  }

  private boolean alreadyExistNickname(String nickname) {
    return userRepository.findByNickname(nickname).isPresent();
  }

  private boolean alreadyExistEmail(String email) {
    return userRepository.findByEmail(email).isPresent();
  }
}
