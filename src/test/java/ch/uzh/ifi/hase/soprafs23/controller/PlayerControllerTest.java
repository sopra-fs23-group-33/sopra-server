package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.Betting.Result;
import ch.uzh.ifi.hase.soprafs23.Data.PlayerData;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        MockHttpServletRequestBuilder putRequest = MockMvcRequestBuilders.put("/players/1/bet")
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
        MockHttpServletRequestBuilder putRequest = MockMvcRequestBuilders.put("/players/1/bet")
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
        MockHttpServletRequestBuilder putRequest = MockMvcRequestBuilders.put("/players/1/bet")
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
    