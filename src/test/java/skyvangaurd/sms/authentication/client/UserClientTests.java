package skyvangaurd.sms.authentication.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties.Consumer.Subscription;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import skyvangaurd.sms.authentication.dto.LoginRequest;
import skyvangaurd.sms.authentication.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.ZoneId;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserClientTests {

  @LocalServerPort
  private int port;

  @Autowired
  TestRestTemplate restTemplate;

  private User userFromDb;
  private HttpHeaders headers;
  String authHeader;

  @BeforeEach
  public void setup() {
    headers = new HttpHeaders();
  }

  @AfterEach
  public void teardown() {
    userFromDb = null;
    headers = null;
    authHeader = null;
  }

  @Test
  public void shouldHaveValidToken() {
    getValidTestUser();
    ResponseEntity<String> loginResponse = doLogin();
    String token = loginResponse.getBody();
    headers.set("Authorization", "Bearer " + token);
    authHeader = headers.getFirst("Authorization");
    
    assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(authHeader);
    assertTrue(authHeader.equals("Bearer " + token));

    ResponseEntity<String> logoutResponse = doLogout();
    String logoutMessage = logoutResponse.getBody();

    assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertEquals(logoutMessage, "Successfully logged out");
  }

  @Test
  public void shouldNotHaveValidToken() {
    getInvalidTestUser();

    ResponseEntity<String> loginResponse = doLogin();
    assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertEquals(loginResponse.getBody(), "Incorrect username or password");
  }

  private LoginRequest convertToUserDto(User user) {
    LoginRequest userRegistrationDto = new LoginRequest(
        user.getEmail(),
        user.getPassword(),
        user.getRoles().stream().toList());
    return userRegistrationDto;
  }

  private void getValidTestUser() {
    String dateString = "2024-02-23";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate localDate = LocalDate.parse(dateString, formatter);
    Date today = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    userFromDb = new User();
    userFromDb.setEmail("user1@example.com");
    userFromDb.setPassword("changeme");
    userFromDb.setIsActive(true);
    userFromDb.setCreatedAt(today);
    userFromDb.setUpdatedAt(today);
  }

  private void getInvalidTestUser() {
    String dateString = "2024-02-23";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate localDate = LocalDate.parse(dateString, formatter);
    Date today = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    userFromDb = new User();
    userFromDb.setEmail("invalidUser@example.com");
    userFromDb.setPassword("invalid");
    userFromDb.setIsActive(true);
    userFromDb.setCreatedAt(today);
    userFromDb.setUpdatedAt(today);
  }

  private ResponseEntity<String> doLogin() {
    String loginUrl = createURLWithPort("/api/auth/login");
    LoginRequest loginRequestDto = convertToUserDto(userFromDb);
    HttpEntity<LoginRequest> loginRequest = new HttpEntity<>(loginRequestDto, headers);

    return restTemplate.postForEntity(loginUrl, loginRequest, String.class);
  }

  private ResponseEntity<String>  doLogout() {
    String logoutUrl = createURLWithPort("/api/auth/logout");
    HttpEntity<String> logoutRequest = new HttpEntity<>(null, headers);
    return restTemplate.postForEntity(logoutUrl, logoutRequest, String.class);
  }

  private String createURLWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }
}
