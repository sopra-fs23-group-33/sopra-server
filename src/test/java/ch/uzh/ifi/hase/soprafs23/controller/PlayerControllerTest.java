package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.Betting.Result;
import ch.uzh.ifi.hase.soprafs23.Data.PlayerData;
import ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents.AbstractPowerUp;
import ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents.PowerupX2;
import ch.uzh.ifi.hase.soprafs23.constant.Direction;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.BetPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.ResultGetDTO;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PlayerControllerTest
 * This is a WebMvcTest which allows to test the PlayerController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the PlayerController works.
 */
@WebMvcTest(PlayerController.class)
class PlayerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    User user;
    Player player;
    PlayerData data;
    BetPutDTO betToSend;
    Result result;
    ResultGetDTO resultDTO;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private GameService gameService;

    @MockBean 
    private UserService userService;

    @BeforeEach
    void setup() {
        user = new User("username", "password123?");
        player = new Player(user);
        data = player.status();

        betToSend = new BetPutDTO();
        betToSend.setAmount(100);
        betToSend.setType(Direction.UP);

        result = new Result(Direction.UP, 10, 100);

        resultDTO = new ResultGetDTO();
        resultDTO.setOutcome(Direction.UP);
        resultDTO.setBettingAmount(100);
        resultDTO.setProfit(1);        
    }

    @Test
    void getPlayerValid() throws Exception {
        // this mocks the PlayerService -> we define above what the playerService should
        // return when getPlayerByPlayerID() is called
        given(playerService.getPlayerByPlayerID(Mockito.any())).willReturn(player);

        // when
        MockHttpServletRequestBuilder getRequest = get("/players/1")
            .contentType(MediaType.APPLICATION_JSON)
            .header("token", user.getToken());

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
        Throwable response = new ResponseStatusException(HttpStatus.NOT_FOUND, "Player with playerId " + player.getPlayerID() + " was not found");
        given(playerService.getPlayerByPlayerID(Mockito.any())).willThrow(response);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/players/1").contentType(MediaType.APPLICATION_JSON).header("token", user.getToken());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    void placeBetValid() throws Exception {        
        // this mocks the PlayerService -> we define above what the playerService should
        // return when getPlayerByPlayerID() is called
        Mockito.doNothing().when(playerService).placeBet(Mockito.any(), Mockito.any());

        // when
        MockHttpServletRequestBuilder putRequest = put("/players/1/bet")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(betToSend))
            .header("token", user.getToken());

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    void placeBetInvalidBet() throws Exception {
        Throwable response = new ResponseStatusException(HttpStatus.CONFLICT, "Error");
        Mockito.doThrow(response).when(playerService).placeBet(Mockito.any(), Mockito.any());

        // when
        MockHttpServletRequestBuilder putRequest = put("/players/1/bet")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(betToSend))
            .header("token", user.getToken());

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isConflict());
    }

    @Test
    void placeBetInvalidToken() throws Exception {
        Throwable response = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "provided token does not match requested player with 1");
        Mockito.doThrow(response).when(playerService).tokenMatch(Mockito.any(), Mockito.any());

        // when
        MockHttpServletRequestBuilder putRequest = put("/players/1/bet")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(betToSend))
            .header("token", user.getToken());

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getResultValid() throws Exception {        
        // this mocks the PlayerService -> we define above what the playerService should
        // return when getPlayerByPlayerID() is called
        given(playerService.getResult(Mockito.any())).willReturn(result);

        // when
        MockHttpServletRequestBuilder getRequest = get("/players/1/result")
            .contentType(MediaType.APPLICATION_JSON)
            .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outcome", is(result.getOutcome().toString())))
                .andExpect(jsonPath("$.profit", is(result.getProfit())))
                .andExpect(jsonPath("$.bettingAmount", is(result.getBettingAmount())));
    }

    @Test
    void getResultInvalidID() throws Exception {        
        // this mocks the PlayerService -> we define above what the playerService should
        // return when getPlayerByPlayerID() is called
        Throwable response = new ResponseStatusException(HttpStatus.NOT_FOUND, "Player with playerId " + player.getPlayerID() + " was not found");
        given(playerService.getResult(Mockito.any())).willThrow(response);

        // when
        MockHttpServletRequestBuilder getRequest = get("/players/1/result")
            .contentType(MediaType.APPLICATION_JSON)
            .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    void getPowerups() throws Exception {
        List<AbstractPowerUp> allPowerups = Collections.singletonList(new PowerupX2(1L, "test"));

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        given(playerService.getPowerups(Mockito.any())).willReturn(allPowerups);

        MockHttpServletRequestBuilder getRequest = get("/players/1/powerups")
                .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].powerupType", is("X2")))
                .andExpect(jsonPath("$[0].ownerName", is("test")))
                .andExpect(jsonPath("$[0].ownerID", is(1)))
                .andExpect(jsonPath("$[0].name", is("X2")))
                .andExpect(jsonPath("$[0].description", is("this powerup doubles your gain or loss")))
                .andExpect(jsonPath("$[0].active", is(false)));
    }

    @Test
    void activatePowerup() throws Exception {
        Mockito.doNothing().when(userService).checkToken(user.getToken());
        Mockito.doNothing().when(playerService).activatePowerup(Mockito.any(), Mockito.any());

        MockHttpServletRequestBuilder getRequest = put("/players/1/powerups/2")
                .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isNoContent());
    }

    @Test
    void activatePowerupInvalid() throws Exception {
        Mockito.doNothing().when(userService).checkToken(user.getToken());
        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(playerService).activatePowerup(Mockito.any(), Mockito.any());

        MockHttpServletRequestBuilder getRequest = put("/players/1/powerups/3")
                .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

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
    