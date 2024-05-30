package com.kh.bookfinder.book.service;

import com.kh.bookfinder.book.dto.BookRequestDto;
import com.kh.bookfinder.book.dto.SearchDto;
import com.kh.bookfinder.book.entity.ApprovalStatus;
import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.repository.BookRepository;
import com.kh.bookfinder.global.constants.Message;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  public Book findBook(Long isbn) {
    return bookRepository.findByIsbn(isbn)
        .orElseThrow(() -> new ResourceNotFoundException(Message.NOT_FOUND_BOOK));
  }

  public Book findApprovedBook(Long isbn) {
    return bookRepository.findByIsbnAndApprovalStatus(isbn, ApprovalStatus.APPROVE)
        .orElseThrow(() -> new ResourceNotFoundException(Message.NOT_FOUND_BOOK));
  }

  public List<Book> getBooks(SearchDto requestParam) {
    if (requestParam.getApprovalStatus() == ApprovalStatus.APPROVE) {
      return bookRepository
          .findApprovedBooksByFilterAndKeywordContaining(requestParam.getFilter(), requestParam.getKeyword());
    }

    return bookRepository
        .findNotApprovedBooksByFilterAndKeywordContaining(requestParam.getFilter(), requestParam.getKeyword());
  }

  @Transactional
  public void updateStatus(Long isbn, ApprovalStatus approvalStatus) {
    Book book = findBook(isbn);
    book.setApprovalStatus(approvalStatus);
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