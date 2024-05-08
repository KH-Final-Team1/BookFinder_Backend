package com.kh.bookfinder.dto;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.constants.Regexp;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BookDTO {

    @Pattern(regexp = Regexp.ISBN, message = Message.INVALID_ISBN_DIGITS)
    private Long isbn;
}
