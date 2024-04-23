package com.kh.bookfinder.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.kh.bookfinder.entity.Address;
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
public class AddressRepositoryTest {

  @Autowired
  private AddressRepository addressRepository;

  @Test
  public void addressInsertTest() {
    // Given: Address가 주어진다.
    Address givenAddress = Address.builder()
        .si("서울특별시")
        .gu("강남구")
        .dong("역삼동")
        .roadFullAddress("서울 강남구 테헤란로 10길 9")
        .build();

    // When: save() 메서드를 호출하여 givenAddress를 저장한다.
    Address actual = this.addressRepository.save(givenAddress);

    // Then: actual은 givenAddress와 같아야한다.
    assertThat(actual).isEqualTo(givenAddress);
  }

}
