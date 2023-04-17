package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.Runner.GameRunner;
import ch.uzh.ifi.hase.soprafs23.constant.UserState;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PlayerControllerTest
 * This is a WebMvcTest which allows to test the PlayerController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the PlayerController works.
 */
@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    User user;
    //Player player;
    //PlayerData data;
    //BetPutDTO betToSend;
    //Result result;
    //ResultGetDTO resultDTO;
    GamePostDTO gamePostDTO;
    GameData gameData;
    Game game;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private UserService userService;

    @MockBean
    private GameService gameService;

    @MockBean
    private PlayerController playerController;

    @MockBean
    private GameRunner gameRunner;

    
    @BeforeEach
    void setup() {
        user = new User("username", "password123?");
        //player = new Player(user);
        //data = player.status();

        //betToSend = new BetPutDTO();
        //betToSend.setAmount(100);
        //betToSend.setType(Direction.UP);

        //result = new Result(Direction.UP, 10, 100);

        //resultDTO = new ResultGetDTO();
        //resultDTO.setOutcome(Direction.UP);
        //resultDTO.setBettingAmount(100);
        //resultDTO.setProfit(1); 
        
        gamePostDTO = new GamePostDTO();
        gamePostDTO.setCreator("username");
        gamePostDTO.setEventsActive(false);
        gamePostDTO.setName("game1");
        gamePostDTO.setNumberOfRoundsToPlay(5);
        gamePostDTO.setPowerupsActive(false);
        gamePostDTO.setTotalLobbySize(3);
        gamePostDTO.setTypeOfGame("MULTIPLAYER");

        gameData =  DTOMapper.INSTANCE.convertGamePostDTOToGameData(gamePostDTO);

        game = new Game(user, gameData);
        game.setGameID(1L);
        game.init();
    } 

    @Test
    void createGameValid() throws Exception {
        Mockito.doNothing().when(userService).checkToken(user.getToken());
        given(userService.getUserByUsername(user.getUsername())).willReturn(user);
        given(gameService.createGame(Mockito.any(), Mockito.any())).willReturn(game);

        // when
        
        MockHttpServletRequestBuilder postRequest = post("/games/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(gamePostDTO))
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gameID", is(1)))
                .andExpect(jsonPath("$.status", is("LOBBY")))
                .andExpect(jsonPath("$.name", is(gamePostDTO.getName())))
                .andExpect(jsonPath("$.typeOfGame", is(gamePostDTO.getTypeOfGame())))
                .andExpect(jsonPath("$.powerupsActive", is(false)))
                .andExpect(jsonPath("$.eventsActive", is(false)))
                .andExpect(jsonPath("$.timer", is(0)))
                .andExpect(jsonPath("$.totalLobbySize", is(gamePostDTO.getTotalLobbySize())))
                .andExpect(jsonPath("$.numberOfPlayersInLobby", is(1)))
                .andExpect(jsonPath("$.numberOfRoundsToPlay", is(gamePostDTO.getNumberOfRoundsToPlay())))
                .andExpect(jsonPath("$.currentRoundPlayed", is(0)))
                .andExpect(jsonPath("$.event", org.hamcrest.CoreMatchers.nullValue()))
                .andExpect(jsonPath("$.creator", is(gamePostDTO.getCreator())));
    }

    @Test
    void createGameInvalidUserState() throws Exception {
        user.setState(UserState.OFFLINE);
        String ErrorMessage = "cannot Create game because user is still in an ongoing game or offline";
        Throwable response = new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        given(userService.getUserByUsername(user.getUsername())).willReturn(user);
        given(gameService.createGame(Mockito.any(), Mockito.any())).willThrow(response);

        // when
        
        MockHttpServletRequestBuilder postRequest = post("/games/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(gamePostDTO))
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());
    }

    @Test
    void createGameInvalidGameData() throws Exception {
        gamePostDTO.setName("????????????????????????????????????????????????????????????????");
        String ErrorMessage = "Invalid name: Does not contain alphabetic characters.\nInvalid name: Contains invalid characters.\nInvalid name: Too long or empty.\n";
        Throwable response = new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        given(userService.getUserByUsername(user.getUsername())).willReturn(user);
        given(gameService.createGame(Mockito.any(), Mockito.any())).willThrow(response);

        // when
        
        MockHttpServletRequestBuilder postRequest = post("/games/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(gamePostDTO))
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());
    }

    @Test
    void createGameInvalidToken() throws Exception {
        Throwable response = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The token (" + user.getToken() + ")  " + "is invalid");

        Mockito.doThrow(response).when(userService).checkToken(user.getToken());
        given(userService.getUserByUsername(user.getUsername())).willReturn(user);
        given(gameService.createGame(Mockito.any(), Mockito.any())).willThrow(response);

        // when
        
        MockHttpServletRequestBuilder postRequest = post("/games/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(gamePostDTO))
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getGameByGameIDValid() throws Exception {
        Mockito.doNothing().when(userService).checkToken(user.getToken());
        given(gameService.getGameByGameID(game.getGameID())).willReturn(game);

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(gamePostDTO))
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameID", is(1)))
                .andExpect(jsonPath("$.status", is("LOBBY")))
                .andExpect(jsonPath("$.name", is(gamePostDTO.getName())))
                .andExpect(jsonPath("$.typeOfGame", is(gamePostDTO.getTypeOfGame())))
                .andExpect(jsonPath("$.powerupsActive", is(false)))
                .andExpect(jsonPath("$.eventsActive", is(false)))
                .andExpect(jsonPath("$.timer", is(0)))
                .andExpect(jsonPath("$.totalLobbySize", is(gamePostDTO.getTotalLobbySize())))
                .andExpect(jsonPath("$.numberOfPlayersInLobby", is(1)))
                .andExpect(jsonPath("$.numberOfRoundsToPlay", is(gamePostDTO.getNumberOfRoundsToPlay())))
                .andExpect(jsonPath("$.currentRoundPlayed", is(0)))
                .andExpect(jsonPath("$.event", org.hamcrest.CoreMatchers.nullValue()))
                .andExpect(jsonPath("$.creator", is(gamePostDTO.getCreator())));
    }    

    @Test
    void getGameByGameIDInvalidID() throws Exception {
        String ErrorMessage = "Game with gameId " + game.getGameID() + " was not found";
        Throwable response = new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        given(gameService.getGameByGameID(game.getGameID())).willThrow(response);

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(gamePostDTO))
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    void startValid() throws Exception {
        Mockito.doNothing().when(userService).checkToken(user.getToken());
        Mockito.doNothing().when(gameService).start(1L, user.getToken()); // revise
        
        MockHttpServletRequestBuilder postRequest = MockMvcRequestBuilders.post("/games/1/start")
            .header("token", user.getToken());

        mockMvc.perform(postRequest)
            .andExpect(status().isNoContent());
        
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