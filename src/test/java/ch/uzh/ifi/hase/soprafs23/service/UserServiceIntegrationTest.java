package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserState;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @BeforeEach
  void setup() {
    userRepository.deleteAll();
  }

  @Test
  void createUser_validInputs_success() {
    // given
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setPassword("testPwd");
    testUser.setUsername("testUsername");

    // when
    User createdUser = userService.createUser(testUser);

    // then
    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getPassword(), createdUser.getPassword());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(testUser.getRank(), -1);
    assertEquals(testUser.getNumberOfBetsLost(), 0);
    assertEquals(testUser.getNumberOfBetsWon(), 0);
    assertEquals(testUser.getTotalRoundsPlayed(), 0);
    assertEquals(UserState.ONLINE, createdUser.getState());
  }

  @Test
  void createUser_duplicateUsername_throwsException() {
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setPassword("testPwd");
    testUser.setUsername("testUsername");
    userService.createUser(testUser);

    // attempt to create second user with same username
    User testUser2 = new User();

    // change the name but forget about the username
    testUser2.setPassword("testPwd2");
    testUser2.setUsername("testUsername");

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
  }

  @Test
  void loginUserValid() {
    // given
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setPassword("testPwd");
    testUser.setUsername("testUsername");

    // when
    User createdUser = userService.createUser(testUser);
    User loggedUser = userService.loginUser(testUser);

    // then
    assertEquals(loggedUser.getId(), createdUser.getId());
    assertEquals(loggedUser.getPassword(), createdUser.getPassword());
    assertEquals(loggedUser.getUsername(), createdUser.getUsername());
    assertEquals(loggedUser.getToken(), createdUser.getToken());
    assertEquals(loggedUser.getRank(), createdUser.getRank());
    assertEquals(loggedUser.getNumberOfBetsLost(), testUser.getNumberOfBetsLost());
    assertEquals(loggedUser.getNumberOfBetsWon(), testUser.getNumberOfBetsWon());
    assertEquals(loggedUser.getTotalRoundsPlayed(), testUser.getTotalRoundsPlayed());
    assertEquals(UserState.ONLINE, loggedUser.getState());
  }

  @Test
  void loginUserInvalidUsername() {
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setPassword("testPwd");
    testUser.setUsername("testUsername");
    userService.createUser(testUser);

    // attempt to create second user with same username
    User testUser2 = new User();

    // change the name but forget about the username
    testUser2.setPassword("testPwd");
    testUser2.setUsername("testUsername2");

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser2));
  }

  @Test
  void loginUserInvalidCombination() {
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setPassword("testPwd");
    testUser.setUsername("testUsername");
    userService.createUser(testUser);

    // attempt to create second user with same username
    User testUser2 = new User();

    // change the name but forget about the username
    testUser2.setPassword("testPwd2");
    testUser2.setUsername("testUsername");

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser2));
  }
}
