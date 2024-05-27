package com.kh.bookfinder.borough.repository;

import com.kh.bookfinder.borough.entity.Borough;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoroughRepository extends JpaRepository<Borough, Long> {

  Optional<Borough> findById(Long id);

  Optional<Borough> findByName(String name);
}
