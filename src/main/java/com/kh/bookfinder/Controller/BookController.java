package com.kh.bookfinder.controller;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.dto.BookRequestDTO;
import com.kh.bookfinder.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BookController {

    @Autowired
    private BookService bSerivce;

    @ResponseBody
    @GetMapping(value = "api/v1/request/check/isbn", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Object> selectBookRequestIsbn(@RequestParam @Valid BookRequestDTO bookRequestDTO)
            throws JSONException{
        bSerivce.IsbnDuplicate(bookRequestDTO);
        JSONObject responseBody = new JSONObject();
        responseBody.put("message", Message.SUCCESS_ISBN);
        return ResponseEntity.ok().body(responseBody.toString());


    }
}