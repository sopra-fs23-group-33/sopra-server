package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.constant.UserState;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
  @Test
  public void testCreateUser_fromUserPostDTO_toUser_success() {
    // create UserPostDTO
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("name");
    userPostDTO.setUsername("username");

    // MAP -> Create user
    User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // check content
    assertEquals(userPostDTO.getPassword(), user.getPassword());
    assertEquals(userPostDTO.getUsername(), user.getUsername());
  }

  @Test
  public void testGetUser_fromUser_toUserGetDTO_success() {
    // create User
    User user = new User();
    user.setId(12321L);
    user.setUsername("firstname@lastname");
    user.setToken("token1");
    user.setCreationDate(LocalDate.parse("2023-04-01"));
    user.setTotalRoundsPlayed(0);
    user.setNumberOfBetsWon(0);
    user.setNumberOfBetsLost(0);
    user.setRank(-1);
    user.setState(UserState.OFFLINE);

    // MAP -> Create UserGetDTO
    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

    // check content
    assertEquals(user.getId(), userGetDTO.getId());
    assertEquals(user.getUsername(), userGetDTO.getUsername());
    assertEquals(user.getState(), userGetDTO.getState());
    assertEquals(user.getToken(), userGetDTO.getToken());
    assertEquals(user.getCreationDate(), userGetDTO.getCreationDate());
    assertEquals(user.getTotalRoundsPlayed(), userGetDTO.getTotalRoundsPlayed());
    assertEquals(user.getNumberOfBetsLost(), userGetDTO.getNumberOfBetsLost());
    assertEquals(user.getNumberOfBetsWon(), userGetDTO.getNumberOfBetsWon());
    assertEquals(user.getRank(), userGetDTO.getRank());
  }
}
