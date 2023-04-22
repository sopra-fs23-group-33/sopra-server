package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        userRepository.flush();
    }

    @AfterEach
    void delete(){
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void createUser_validInputs_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPwd1?");
        testUser.setUsername("testUsername");

        // when
        User createdUser = userService.createUser(testUser);

        // then
        //assertEquals(createdUser.getUserID(), 1);
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(1, createdUser.getRank());
        assertEquals(0, createdUser.getNumberOfBetsLost());
        assertEquals(0, createdUser.getNumberOfBetsWon());
        assertEquals(0, createdUser.getTotalRoundsPlayed());
        assertEquals(UserStatus.ONLINE, createdUser.getStatus());
    }

    @Test
    void createUser_duplicateUsername_throwsException() {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPwd2?");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        // attempt to create second user with same username
        User testUser2 = new User();

        // change the name but forget about the username
        testUser2.setPassword("testPwd2?");
        testUser2.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
    }

    @Test
    void loginUserValid() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPwd2?");
        testUser.setUsername("testUsername");

        // when
        User createdUser = userService.createUser(testUser);
        User loggedUser = userService.loginUser(testUser);

        // then
        assertEquals(loggedUser.getUserID(), createdUser.getUserID());
        assertEquals(loggedUser.getPassword(), createdUser.getPassword());
        assertEquals(loggedUser.getUsername(), createdUser.getUsername());
        assertNotNull(loggedUser.getToken());
        //assertEquals(loggedUser.getToken(), createdUser.getToken());
        assertEquals(loggedUser.getRank(), createdUser.getRank());
        assertEquals(loggedUser.getNumberOfBetsLost(), testUser.getNumberOfBetsLost());
        assertEquals(loggedUser.getNumberOfBetsWon(), testUser.getNumberOfBetsWon());
        assertEquals(loggedUser.getTotalRoundsPlayed(), testUser.getTotalRoundsPlayed());
        assertEquals(UserStatus.ONLINE, loggedUser.getStatus());
    }

    @Test
    void loginUserInvalidUsername() {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPwd2?");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        // attempt to create second user with same username
        User testUser2 = new User();

        // change the name but forget about the username
        testUser2.setPassword("testPwd2?");
        testUser2.setUsername("testUsername2");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser2));
    }

    @Test
    void loginUserStillPlaying() {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPwd2?");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        //change UserStatus to playing
        User testUser2 = userRepository.findByUsername(testUser.getUsername());
        testUser2.setStatus(UserStatus.PLAYING);
        userRepository.saveAndFlush(testUser2);

        // attempt to create second user with same username
        User testUser3 = new User();
        testUser3.setPassword("testPwd2?");
        testUser3.setUsername("testUsername");

        //assert that login fails
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser3));
    }

    @Test
    void loginUserInvalidCombination() {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPwd2?");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        // attempt to create second user with same username
        User testUser2 = new User();

        // change the name but forget about the username
        testUser2.setPassword("testPwd1?");
        testUser2.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser2));
    }


    @Test
    void logoutUserValid() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPwd2?");
        testUser.setUsername("testUsername");

        // when
        userService.createUser(testUser);
        User loggedUser = userService.loginUser(testUser);
        userService.logoutUser(loggedUser.getUserID());
        User loggedOutUser = userService.getUserByUserID(loggedUser.getUserID());

        // then
        assertEquals(loggedUser.getUserID(), loggedOutUser.getUserID());
        assertEquals(loggedUser.getPassword(), loggedOutUser.getPassword());
        assertEquals(loggedUser.getUsername(), loggedOutUser.getUsername());
        assertEquals(loggedUser.getRank(), loggedOutUser.getRank());
        assertEquals(loggedUser.getNumberOfBetsLost(), loggedOutUser.getNumberOfBetsLost());
        assertEquals(loggedUser.getNumberOfBetsWon(), loggedOutUser.getNumberOfBetsWon());
        assertEquals(loggedUser.getTotalRoundsPlayed(), loggedOutUser.getTotalRoundsPlayed());
        assertEquals(UserStatus.OFFLINE, loggedOutUser.getStatus());
    }

    @Test
    void logoutUserInvalidState() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPwd2?");
        testUser.setUsername("testUsername");

        // when
        userService.createUser(testUser);
        User loggedUser = userService.loginUser(testUser);
        userService.logoutUser(loggedUser.getUserID());

        // check that an error is thrown
        Long id = loggedUser.getUserID();
        assertThrows(ResponseStatusException.class, () -> userService.logoutUser(id));
    }

    @Test
    void checkTokenValid() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPwd2?");
        testUser.setUsername("testUsername");

        // when
        userService.createUser(testUser);
        User loggedUser = userService.loginUser(testUser);
        userService.logoutUser(loggedUser.getUserID());

        // check that an error is thrown
        assertDoesNotThrow(() -> userService.checkToken(loggedUser.getToken()));
    }

    @Test
    void checkTokenInvalid() {
        // given
        String token = "testToken";
        assertNull(userRepository.findByToken(token));

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.checkToken(token));
        ;
    }

    @Test
    void checkTestToken() {
        // given
        String token = "test123";
        assertNull(userRepository.findByToken(token));
        // check that an error is thrown
        assertDoesNotThrow(() -> userService.checkToken(token));
        ;
    }

    @Test
    void nameAndPasswordValid() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPwd1234?!");
        testUser.setUsername("testUsername");

        // check that an error is not thrown
        assertDoesNotThrow(() -> userService.createUser(testUser));
    }

    @Test
    void nameInvalidAlphabeticMissing() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPwd1234?!");
        testUser.setUsername("12321");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    void nameInvalidIlegalChar() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPwd1234?!");
        testUser.setUsername("AB()BA");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    void nameInvalidTooLong() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPwd1234?!");
        testUser.setUsername("BioJohgurtNature35FettImMilchanteilZutatenMilchMilchproteinMindestensHaltnarBisSieheDeckel");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    void pwdInvalidSpecialMissing() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPwd1234");
        testUser.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    void pwdInvalidTooShort() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("T1?");
        testUser.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    void pwdInvalidTooLong() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("12356789123456789?MilchanteilFett35@@@@@@@@@@@@@@@");
        testUser.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    void pwdInvalidIlegalChar() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPwd1234?()");
        testUser.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    void pwdNoAlphabeticCharacter() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("123456)&2!1");
        testUser.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    void pwdNoNumber() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("TestPwd!/&");
        testUser.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    void getUserByIDNotFound() {
        assertThrows(ResponseStatusException.class, () -> userService.getUserByUserID(200000L));
    }

    @Test
    void LeaderBoard() {
        List<User> leaderboard = this.userService.leaderboard();

        assertTrue(leaderboard.isEmpty());

        User testUser1 = new User("User1", "TestPwd2?");
        testUser1 = userService.createUser(testUser1);

        User testUser2 = new User("User2", "TestPwd2?");
        testUser2 = userService.createUser(testUser2);

        User testUser3 = new User("User3", "TestPwd2?");
        testUser3 = userService.createUser(testUser3);

        User testUser4 = new User("User4", "TestPwd2?");
        testUser4 = userService.createUser(testUser4);

        User bestUser = userService.getUserByUsername(testUser3.getUsername());
        bestUser.roundWon();
        bestUser.roundWon();
        bestUser.roundWon();
        bestUser.roundLost();
        userRepository.saveAndFlush(bestUser);

        User secondBestUser = userService.getUserByUsername(testUser2.getUsername());
        secondBestUser.roundWon();
        secondBestUser.roundLost();
        userRepository.saveAndFlush(secondBestUser);

        leaderboard = this.userService.leaderboard();

        assertEquals(testUser3.getUsername(), leaderboard.get(0).getUsername());
        assertEquals(testUser2.getUsername(), leaderboard.get(1).getUsername());
        assertEquals(testUser1.getUsername(), leaderboard.get(2).getUsername());
        assertEquals(testUser4.getUsername(), leaderboard.get(3).getUsername());

        assertEquals(0.75, leaderboard.get(0).getWinRate());
        assertEquals(0.5, leaderboard.get(1).getWinRate());
        assertEquals(0.0, leaderboard.get(2).getWinRate());
        assertEquals(0.0, leaderboard.get(3).getWinRate());
    }

}

