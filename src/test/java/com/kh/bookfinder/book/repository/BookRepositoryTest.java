package com.kh.bookfinder.book.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.repository.BookRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BookRepositoryTest {

  @Autowired
  BookRepository bookRepository;

  @Test
  public void bookInsertAndSelectOneTest() {
    // Given: book 데이터가 주어진다.
    Book given = Book.builder()
        .isbn(9791162243091L)
        .additionSymbol(94000)
        .name("혼자 공부하는 컴퓨터구조 + 운영체제 :1:1 과외하듯 배우는 컴퓨터 공학 전공 지식 자습서 ")
        .authors("강민철 지음")
        .publisher("한빛미디어")
        .publicationYear(2022)
        .publicationDate(LocalDate.of(2022, 8, 16))
        .classNo("004")
        .className("총류 > 총류 > 전산학")
        .imageUrl("https://image.aladin.co.kr/product/29901/42/cover/k932838038_1.jpg")
        .description(
            "1:1 과외하듯 배우는 IT 지식 입문서. 독학으로 컴퓨터 구조와 운영체제를 배우는 입문자가 ‘꼭 필요한 내용을 제대로 학습’할 수 있도록 구성했다. 뭘 모르는지조차 모르는 입문자의 막연한 마음에 십분 공감하여 과외 선생님이 알려주듯 친절하게, 핵심 내용만 콕콕 집어 준다.")
        .build();

    // When: save() 메서드를 호출한다.
    this.bookRepository.save(given);

    // Then: 추가된 데이터는 given과 같다.
    Book actual = this.bookRepository.findById(given.getIsbn()).orElse(null);
    assertThat(actual).isNotNull();
    assertThat(actual.getIsbn()).isEqualTo(given.getIsbn());
    assertThat(actual.getAdditionSymbol()).isEqualTo(given.getAdditionSymbol());
    assertThat(actual.getName()).isEqualTo(given.getName());
    assertThat(actual.getAuthors()).isEqualTo(given.getAuthors());
    assertThat(actual.getPublisher()).isEqualTo(given.getPublisher());
    assertThat(actual.getPublicationYear()).isEqualTo(given.getPublicationYear());
    assertThat(actual.getPublicationDate()).isEqualTo(given.getPublicationDate());
    assertThat(actual.getClassNo()).isEqualTo(given.getClassNo());
    assertThat(actual.getClassName()).isEqualTo(given.getClassName());
    assertThat(actual.getImageUrl()).isEqualTo(given.getImageUrl());
    assertThat(actual.getDescription()).isEqualTo(given.getDescription());
  }

  @Test
  @Sql("classpath:insert.sql")
  public void selectListTest() {
    // Given: @Sql을 통해 데이터를 5개 추가한다.

    // When: findAll() 메서드를 호출한다.
    List<Book> actual = this.bookRepository.findAll();

    // Then: actual의 size는 5이다.
    assertThat(actual).hasSize(5);
  }

  @Test
  @Sql("classpath:insert.sql")
  public void updateTest() {
    // Given: @Sql을 통해 데이터를 5개 추가한다.
    // And: 하나의 Book 객체를 가져온다.
    Book given = this.bookRepository.findAll().get(0);
    Long isbn = given.getIsbn();
    String updateName = "update book";

    // When: given의 name을 "update book"으로 수정 후 flush()를 호출한다.
    given.setName(updateName);

    // Then: 수정한 update book으로 반영된다.
    Book actual = this.bookRepository.findById(isbn).orElse(null);
    assertThat(actual).isNotNull();
    assertThat(actual.getName()).isEqualTo(updateName);
  }

  @Test
  @Sql("classpath:insert.sql")
  public void deleteTest() {
    // Given: @Sql을 통해 데이터를 5개 추가한다.
    // And: 하나의 Book 객체의 isbn을 가져온다.
    Long isbn = this.bookRepository.findAll().get(0).getIsbn();

    // When: deleteBytId() 메서드를 호출한다.
    this.bookRepository.deleteById(isbn);

    // Then: 저장된 데이터는 4개이다.
    List<Book> actual = this.bookRepository.findAll();
    assertThat(actual).hasSize(4);
  }

}
