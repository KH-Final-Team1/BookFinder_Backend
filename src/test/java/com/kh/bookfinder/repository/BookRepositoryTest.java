package com.kh.bookfinder.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.kh.bookfinder.entity.Book;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BookRepositoryTest {

  @Autowired
  BookRepository bookRepository;

  @Test
  @DisplayName("리스트의 크기는 5이어야 한다.")
  public void bookListTest() {

    List<Book> books = this.bookRepository.findAll();

    assertThat(books.size()).isNotEqualTo(0);
  }

}
