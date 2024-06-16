package com.kh.bookfinder.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


public class JsonParsingSqlTest {

  private static final String API_KEY = "";

  @Test
  public void test1() throws IOException, JSONException {
    List<String> isbnList = extractIsbnList();

    RestTemplate restTemplate = new RestTemplate();
    String baseUrl = "http://data4library.kr/api/srchDtlList";

    FileWriter fileWriter = new FileWriter("data.sql", StandardCharsets.UTF_8, true);

    for (String isbn : isbnList) {
      String uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
          .queryParam("authKey", API_KEY)
          .queryParam("isbn13", isbn)
          .queryParam("format", "json")
          .build().toUriString();
      URL url = new URL(uri);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");

      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuilder content = new StringBuilder();
      while ((inputLine = in.readLine()) != null) {
        content.append(inputLine);
      }
      in.close();
      con.disconnect();

      String response = content.toString();
      JSONObject book = new JSONObject(response)
          .getJSONObject("response")
          .getJSONArray("detail")
          .getJSONObject(0)
          .getJSONObject("book");

      String insertSql = """
          INSERT INTO BOOK(ISBN, NAME, AUTHORS, PUBLISHER, PUBLICATION_YEAR, CLASS_NO, CLASS_NAME, IMAGE_URL, DESCRIPTION, APPROVAL_STATUS)
          VALUES(%s, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', 'APPROVE');
          """;
      String result = String.format(insertSql,
          book.getLong("isbn13"), book.getString("bookname"), book.getString("authors"),
          book.getString("publisher"), book.getString("publication_year"),
          book.getString("class_no"), book.getString("class_nm"), book.getString("bookImageURL"),
          book.getString("description"));

      fileWriter.write(result);
    }

    fileWriter.close();
  }

  private List<String> extractIsbnList() throws IOException, JSONException {
    ClassPathResource resource = new ClassPathResource("test.json");
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readValue(resource.getFile(), JsonNode.class);
    JSONObject jsonObject = new JSONObject(jsonNode.toString());
    JSONArray jsonArray = jsonObject.getJSONArray("docs");

    List<String> isbnList = new ArrayList<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      String isbn = jsonArray.getJSONObject(i).getJSONObject("doc").getString("isbn13");
      isbnList.add(isbn);
    }
    return isbnList;
  }
}
