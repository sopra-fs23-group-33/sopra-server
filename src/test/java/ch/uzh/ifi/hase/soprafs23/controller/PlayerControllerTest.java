package ch.uzh.ifi.hase.soprafs23.controller;


import ch.uzh.ifi.hase.soprafs23.Data.PlayerData;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PlayerControllerTest
 * This is a WebMvcTest which allows to test the PlayerController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the PlayerController works.
 */
@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private GameService gameService;

    @MockBean UserService userService;

    @Test
    void getPlayerValid() throws Exception {
        User user = new User("username", "password123?");
        Player player = new Player(user);
        PlayerData data = player.status();

        // this mocks the PlayerService -> we define above what the playerService should
        // return when getPlayerByPlayerID() is called
        given(playerService.getPlayerByPlayerID(Mockito.any())).willReturn(player);

        // when
        MockHttpServletRequestBuilder getRequest = get("/players/1").contentType(MediaType.APPLICATION_JSON).header("token", user.getToken());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(data.getUsername())))
                .andExpect(jsonPath("$.playerID", is(data.getPlayerID())))
                .andExpect(jsonPath("$.accountBalance", is(data.getAccountBalance())))
                .andExpect(jsonPath("$.numberOfWonRounds", is(data.getNumberOfWonRounds())))
                .andExpect(jsonPath("$.numberOfLostRounds", is(data.getNumberOfLostRounds())))
                .andExpect(jsonPath("$.typeOfCurrentBet", is(data.getTypeOfCurrentBet().toString())));
    }

    @Test
    void getPlayerInvalid() throws Exception {
        // given
        User user = new User("username", "password123?");
        Player player = new Player(user);

        Throwable response = new ResponseStatusException(HttpStatus.NOT_FOUND, "Player with playerId " + player.getPlayerID() + " was not found");
        given(playerService.getPlayerByPlayerID(Mockito.any())).willThrow(response);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/players/1").contentType(MediaType.APPLICATION_JSON).header("token", user.getToken());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }
}
    