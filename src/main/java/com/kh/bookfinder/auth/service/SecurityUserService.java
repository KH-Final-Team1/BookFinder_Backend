package com.kh.bookfinder.auth.service;

import com.kh.bookfinder.auth.dto.SecurityUserDetails;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email).orElse(null);
    return new SecurityUserDetails(user);
  }
}
