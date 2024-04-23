package com.kh.bookfinder.repository;

import com.kh.bookfinder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
