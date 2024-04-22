package com.kh.bookfinder.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;


public class JsonParsingSqlTest {

  @Test
  public void test1() throws IOException, JSONException {
    ClassPathResource resource = new ClassPathResource("test.json");
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readValue(resource.getFile(), JsonNode.class);
    JSONObject jsonObject = new JSONObject(jsonNode.toString());
    JSONArray jsonArray = jsonObject.getJSONArray("docs");
    FileWriter fileWriter = new FileWriter("data.sql", StandardCharsets.UTF_8, true);

    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject book = jsonArray.getJSONObject(i).getJSONObject("doc");

      String insertSql = """
          INSERT INTO BOOK(ISBN, ADDITION_SYMBOL, NAME, AUTHORS, PUBLISHER, PUBLICATION_YEAR, CLASS_NO, CLASS_NAME, IMAGE_URL)
          VALUES(%s, %s, '%s', '%s', '%s', '%s', '%s', '%s', '%s');
          """;
      String result = String.format(insertSql,
          book.getLong("isbn13"), book.getLong("addition_symbol"), book.getString("bookname"),
          book.getString("authors"), book.getString("publisher"), book.getString("publication_year"),
          book.getString("class_no"), book.getString("class_nm"), book.getString("bookImageURL"));

      fileWriter.write(result);
    }
    fileWriter.close();
  }
}
