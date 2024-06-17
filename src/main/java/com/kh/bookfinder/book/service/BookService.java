package com.kh.bookfinder.book.service;

import com.kh.bookfinder.book.dto.ApprovalStatusDto;
import com.kh.bookfinder.book.dto.BookListRequestDto;
import com.kh.bookfinder.book.dto.BookRequestDto;
import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.enums.ApprovalStatus;
import com.kh.bookfinder.book.enums.BookListFilter;
import com.kh.bookfinder.book.repository.BookRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.entity.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;


  public Book findBook(Long isbn) {
    return bookRepository.findByIsbn(isbn)
        .orElseThrow(() -> new ResourceNotFoundException(Message.NOT_FOUND_BOOK));
  }

  public Book getBook(User serviceUser, Long isbn) {
    Book result = bookRepository.findByIsbn(isbn)
        .orElseThrow(() -> new ResourceNotFoundException(Message.NOT_FOUND_BOOK));
    if (serviceUser.isAdmin() || result.getApprovalStatus() == ApprovalStatus.APPROVE) {
      return result;
    }
    throw new AccessDeniedException(Message.NOT_APPROVED_BOOK);
  }

  public List<Book> getBooks(BookListRequestDto requestParam) {
    return bookRepository.findBy(
        BookListFilter.fromStringIgnoreCase(requestParam.getFilter()),
        requestParam.getKeyword(),
        ApprovalStatus.fromStringIgnoreCase(requestParam.getStatus())
    );
  }

  @Transactional
  public void updateStatus(Long isbn, ApprovalStatusDto statusDto) {
    Book book = findBook(isbn);
    book.setApprovalStatus(ApprovalStatus.valueOf(statusDto.getApprovalStatus()));
  }

  @Transactional
  public void requestBook(@Valid BookRequestDto bookRequestDto) {
    Optional<Book> book = bookRepository.findByIsbn(bookRequestDto.getIsbn());
    if (book.isPresent()) {
      ApprovalStatus approvalStatus = book.get().getApprovalStatus();
      if (approvalStatus.equals(ApprovalStatus.APPROVE)) {
        throw new ResourceNotFoundException(Message.DUPLICATE_BOOK_APPROVE);
      } else if (approvalStatus.equals(ApprovalStatus.WAIT)) {
        throw new ResourceNotFoundException(Message.DUPLICATE_BOOK_WAIT);
      } else if (approvalStatus.equals(ApprovalStatus.REJECT)) {
        throw new ResourceNotFoundException(Message.DUPLICATE_BOOK_REJECT);
      }
    }
    bookRepository.save(bookRequestDto.toEntity());
  }
}