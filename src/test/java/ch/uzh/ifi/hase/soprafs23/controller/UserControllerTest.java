package ch.uzh.ifi.hase.soprafs23.controller;


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
    void getAllValid() throws Exception {
        // given
        User user = new User("username", "password");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword(user.getPassword());
        userPostDTO.setUsername(user.getUsername());

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON).header("token", user.getToken());;

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
    void registerValid() throws Exception {
        // given
        User user = new User("username", "password");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword(user.getPassword());
        userPostDTO.setUsername(user.getUsername());

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
    void registerInvalid() throws Exception {
        // given
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

    @Test
    void loginValid() throws Exception {
        // given
        User user = new User("username", "password");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword(user.getPassword());
        userPostDTO.setUsername(user.getUsername());

        given(userService.loginUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));
        ;


        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
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
    void loginInvalidUsername() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("Test User");
        userPostDTO.setUsername("testUsername");

        Throwable response = new ResponseStatusException(HttpStatus.NOT_FOUND, "Username does not exist.");

        given(userService.loginUser(Mockito.any())).willThrow(response);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    void loginInvalidCombination() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("Test User");
        userPostDTO.setUsername("testUsername");

        Throwable response = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username-password combination.");

        given(userService.loginUser(Mockito.any())).willThrow(response);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutValid() throws Exception {
        // given
        String token = "test123";
        Long userID = 1L;

        Mockito.doNothing().when(userService).checkToken(token);
        Mockito.doNothing().when(userService).logoutUser(userID);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users/" + "1" + "/logout")
                        .header("token", "test123");
        ;

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    void logoutInvalidToken() throws Exception {
        // given
        String token = "test123";
        Long userID = 1L;

        Throwable response = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The token (" + token + ")  " + "is invalid");

        Mockito.doThrow(response).when(userService).checkToken(token);
        Mockito.doNothing().when(userService).logoutUser(userID);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users/" + "1" + "/logout")
                        .header("token", "test123");
        ;

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutInvalidState() throws Exception {
        // given
        String token = "test123";
        Long userID = 1L;

        Throwable response = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The token (" + token + ")  " + "is invalid");

        Mockito.doNothing().when(userService).checkToken(token);
        Mockito.doThrow(response).when(userService).logoutUser(userID);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users/" + "1" + "/logout")
                        .header("token", "test123");
        ;

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized());
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
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}