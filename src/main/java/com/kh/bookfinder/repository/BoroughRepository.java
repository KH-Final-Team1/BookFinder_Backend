package com.kh.bookfinder.repository;

import com.kh.bookfinder.entity.Borough;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoroughRepository extends JpaRepository<Borough, Long> {

  Optional<Borough> findById(Long id);

}
