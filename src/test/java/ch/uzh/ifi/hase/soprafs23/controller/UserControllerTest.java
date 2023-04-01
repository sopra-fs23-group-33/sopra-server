package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.UserState;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void getAllValid() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setToken("token1");
    user.setCreationDate(LocalDate.parse("2023-04-01"));
    user.setTotalRoundsPlayed(0);
    user.setNumberOfBetsWon(0);
    user.setNumberOfBetsLost(0);
    user.setRank(-1);
    user.setState(UserState.OFFLINE);

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].token", is(user.getToken())))
        .andExpect(jsonPath("$[0].creationDate", is(user.getCreationDate().toString())))
        .andExpect(jsonPath("$[0].totalRoundsPlayed", is(user.getTotalRoundsPlayed())))
        .andExpect(jsonPath("$[0].numberOfBetsWon", is(user.getNumberOfBetsWon())))
        .andExpect(jsonPath("$[0].numberOfBetsLost", is(user.getNumberOfBetsLost())))
        .andExpect(jsonPath("$[0].rank", is(user.getRank())))
        .andExpect(jsonPath("$[0].state", is(user.getState().toString())));
  }

  @Test
  public void registerValid() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setToken("token1");
    user.setCreationDate(LocalDate.parse("2023-04-01"));
    user.setTotalRoundsPlayed(0);
    user.setNumberOfBetsWon(0);
    user.setNumberOfBetsLost(0);
    user.setRank(-1);
    user.setState(UserState.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("Test User");
    userPostDTO.setUsername("testUsername");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.token", is(user.getToken())))
        .andExpect(jsonPath("$.creationDate", is(user.getCreationDate().toString())))
        .andExpect(jsonPath("$.totalRoundsPlayed", is(user.getTotalRoundsPlayed())))
        .andExpect(jsonPath("$.numberOfBetsWon", is(user.getNumberOfBetsWon())))
        .andExpect(jsonPath("$.numberOfBetsLost", is(user.getNumberOfBetsLost())))
        .andExpect(jsonPath("$.rank", is(user.getRank())))
        .andExpect(jsonPath("$.state", is(user.getState().toString())));
  }

  @Test
  public void registerInvalid() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setToken("token1");
    user.setCreationDate(LocalDate.parse("2023-04-01"));
    user.setTotalRoundsPlayed(0);
    user.setNumberOfBetsWon(0);
    user.setNumberOfBetsLost(0);
    user.setRank(-1);
    user.setState(UserState.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("Test User");
    userPostDTO.setUsername("testUsername");

    Throwable response = new ResponseStatusException(HttpStatus.CONFLICT, "The username provided already exists: the user could not be created.");

    given(userService.createUser(Mockito.any())).willThrow(response);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isConflict());
  }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}