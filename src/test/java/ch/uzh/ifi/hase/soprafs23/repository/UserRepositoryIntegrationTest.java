package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.UserState;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

@DataJpaTest
public class UserRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void findByUsername_success() {
    // given
    User user = new User();
    user.setUserID(1L);
    user.setUsername("firstname@lastname");
    user.setPassword("jiji");
    user.setToken("token1");
    user.setCreationDate(LocalDate.parse("2023-04-01"));
    user.setTotalRoundsPlayed(0);
    user.setNumberOfBetsWon(0);
    user.setNumberOfBetsLost(0);
    user.setRank(-1);
    user.setState(UserState.ONLINE);

    entityManager.merge(user);
    entityManager.flush();

    // when
    User found = userRepository.findByUsername(user.getUsername());

    // then
    assertNotNull(found.getUserID());
    //assertEquals(1, found.getUserID());
    assertEquals(user.getUsername(), found.getUsername());
    assertEquals(user.getState(), found.getState());
    assertEquals(user.getToken(), found.getToken());
    assertEquals(user.getPassword(), found.getPassword());
    assertEquals(user.getCreationDate(), found.getCreationDate());
    assertEquals(user.getTotalRoundsPlayed(), found.getTotalRoundsPlayed());
    assertEquals(user.getNumberOfBetsLost(), found.getNumberOfBetsLost());
    assertEquals(user.getNumberOfBetsWon(), found.getNumberOfBetsWon());
    assertEquals(user.getRank(), found.getRank());
  }
}
