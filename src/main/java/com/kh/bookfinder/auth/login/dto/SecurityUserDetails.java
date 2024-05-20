package com.kh.bookfinder.auth.login.dto;

import com.kh.bookfinder.user.entity.User;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class SecurityUserDetails implements UserDetails {

  private final User serviceUser;

  public SecurityUserDetails(User serviceUser) {
    this.serviceUser = serviceUser;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    String role = serviceUser.getRole().name();
    return List.of(new SimpleGrantedAuthority(role));
  }

  @Override
  public String getPassword() {
    return serviceUser.getPassword();
  }

  @Override
  public String getUsername() {
    return serviceUser.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
