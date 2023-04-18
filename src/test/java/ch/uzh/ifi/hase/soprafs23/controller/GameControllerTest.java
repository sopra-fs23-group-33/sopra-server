package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.Data.ChartData;
import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Data.PlayerData;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.Runner.GameRunner;
import ch.uzh.ifi.hase.soprafs23.constant.Currency;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs23.constant.UserState;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * GameControllerTest
 * This is a WebMvcTest which allows to test the GameController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the GameController works.
 */
@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    UserPostDTO userPostDTO;
    User user;

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
        userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("username");
        userPostDTO.setPassword("password123?");

        user = new User(
            userPostDTO.getUsername(),
            userPostDTO.getPassword()
        );

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
    void getCreatorValid() throws Exception {
        Player player = new Player(user);
        player.setState(PlayerState.ACTIVE);
        PlayerData playerData = player.status();

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        given(gameService.creator(game.getGameID())).willReturn(player);

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameID() + "/creator")
            .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(playerData.getUsername())))
                .andExpect(jsonPath("$.playerID", is(playerData.getPlayerID())))
                .andExpect(jsonPath("$.accountBalance", is(playerData.getAccountBalance())))
                .andExpect(jsonPath("$.numberOfWonRounds", is(playerData.getNumberOfWonRounds())))
                .andExpect(jsonPath("$.numberOfLostRounds", is(playerData.getNumberOfLostRounds())))
                .andExpect(jsonPath("$.typeOfCurrentBet", is(playerData.getTypeOfCurrentBet().toString())));
    }

    @Test
    void getCreatorInvalidGameID() throws Exception {
        String ErrorMessage = "Game with gameId " + game.getGameID() + " was not found";
        Throwable response = new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        given(gameService.creator(game.getGameID())).willThrow(response);

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameID() + "/creator")
            .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    void getCreatorInvalidCreator() throws Exception {
        String ErrorMessage = "Creator was not found";
        Throwable response = new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        given(gameService.creator(game.getGameID())).willThrow(response);

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameID() + "/creator")
            .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    void getStatusValid() throws Exception {
        Mockito.doNothing().when(userService).checkToken(user.getToken());
        given(gameService.getGameByGameID(game.getGameID())).willReturn(game);

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameID() + "/status")
            .header("token", user.getToken());

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
    void getStatusInvalidGameID() throws Exception {
        String ErrorMessage = "Game with gameId " + game.getGameID() + " was not found";
        Throwable response = new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        given(gameService.getGameByGameID(game.getGameID())).willThrow(response);

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameID() + "/status")
            .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test 
    void joinValid() throws Exception {
        Player player = new Player(user);
        //player.setState(PlayerState.ACTIVE);
        PlayerData playerData = player.status();

        Mockito.doNothing().when(userService).checkToken(user.getToken());

        given(gameService.join(user, game.getGameID())).willReturn(player);

        MockHttpServletRequestBuilder postRequest = post("/games/" + game.getGameID() + "/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPostDTO))
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        mockMvc.perform(postRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", is(playerData.getUsername())))
            .andExpect(jsonPath("$.playerID", is(playerData.getPlayerID())))
            .andExpect(jsonPath("$.accountBalance", is(playerData.getAccountBalance())))
            .andExpect(jsonPath("$.numberOfWonRounds", is(playerData.getNumberOfWonRounds())))
            .andExpect(jsonPath("$.numberOfLostRounds", is(playerData.getNumberOfLostRounds())))
            .andExpect(jsonPath("$.typeOfCurrentBet", is(playerData.getTypeOfCurrentBet().toString())));
    }

    @Test 
    void joinInvalidGameID() throws Exception {
        String ErrorMessage = "Game with gameId " + game.getGameID() + " was not found";
        Throwable response = new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        given(gameService.join(Mockito.any(), Mockito.any())).willThrow(response);

        MockHttpServletRequestBuilder postRequest = post("/games/" + 409L + "/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPostDTO))
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        mockMvc.perform(postRequest)
            .andExpect(status().isNotFound());
    }

    @Test 
    void joinInvalidUserState() throws Exception {
        user.setState(UserState.OFFLINE);

        String ErrorMessage = "Conflict";
        Throwable response = new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        given(gameService.join(Mockito.any(), Mockito.any())).willThrow(response);

        MockHttpServletRequestBuilder postRequest = post("/games/" + game.getGameID() + "/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPostDTO))
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        mockMvc.perform(postRequest)
            .andExpect(status().isConflict());
    }

    @Test 
    void joinInvalidUserData() throws Exception {
        user.setUsername("somethingDifferent");

        String ErrorMessage = "Not Found";
        Throwable response = new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        given(gameService.join(Mockito.any(), Mockito.any())).willThrow(response);

        MockHttpServletRequestBuilder postRequest = post("/games/" + game.getGameID() + "/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPostDTO))
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        mockMvc.perform(postRequest)
            .andExpect(status().isNotFound());
    }

    @Test 
    void leaveValid() throws Exception {        
        Mockito.doNothing().when(userService).checkToken(user.getToken());
        Mockito.doNothing().when(gameService).tokenMatch(user.getToken(), game.getGameID());
        Mockito.doNothing().when(gameService).leave(user, game.getGameID());

        MockHttpServletRequestBuilder postRequest = post("/games/" + game.getGameID() + "/leave")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPostDTO))
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        mockMvc.perform(postRequest)
            .andExpect(status().isNoContent());
    }

    @Test 
    void leaveInvalidToken() throws Exception {      
        Throwable response = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The token (" + user.getToken() + ")  " + "is invalid");

        Mockito.doThrow(response).when(userService).checkToken(user.getToken());

        MockHttpServletRequestBuilder postRequest = post("/games/" + game.getGameID() + "/leave")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPostDTO))
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        mockMvc.perform(postRequest)
            .andExpect(status().isUnauthorized());
    }

    @Test 
    void leaveInvalidTokenMatch() throws Exception {      
        String ErrorMessage = "provided token does not match any player in game with gameID: " + game.getGameID();
        Throwable response = new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorMessage);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        Mockito.doThrow(response).when(gameService).tokenMatch(user.getToken(), game.getGameID());

        MockHttpServletRequestBuilder postRequest = post("/games/" + game.getGameID() + "/leave")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPostDTO))
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        mockMvc.perform(postRequest)
            .andExpect(status().isUnauthorized());
    }

    @Test 
    void leaveInvalidUserNotInGame() throws Exception {      
        String ErrorMessage = "Failed to leave game because player is not member of this game";
        Throwable response = new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        Mockito.doNothing().when(gameService).tokenMatch(user.getToken(), game.getGameID());
        Mockito.doThrow(response).when(gameService).leave(user, game.getGameID());

        MockHttpServletRequestBuilder postRequest = post("/games/" + game.getGameID() + "/leave")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPostDTO))
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        mockMvc.perform(postRequest)
            .andExpect(status().isConflict());
    }

    @Test 
    void startValid() throws Exception {        
        Mockito.doNothing().when(userService).checkToken(user.getToken());
        Mockito.doNothing().when(gameService).start(game.getGameID(), user.getToken());

        MockHttpServletRequestBuilder postRequest = post("/games/" + game.getGameID() + "/start")
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        mockMvc.perform(postRequest)
            .andExpect(status().isNoContent());
    }

    @Test
    void startInvalidGameState() throws Exception {
        String ErrorMessage = "Game with gameId " + game.getGameID() + " cannot be started";
        Throwable response = new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        Mockito.doThrow(response).when(gameService).start(1L, user.getToken());
        
        MockHttpServletRequestBuilder postRequest = post("/games/" + game.getGameID() + "/start")
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        mockMvc.perform(postRequest)
            .andExpect(status().isConflict());
        
    }

    @Test
    void startInvalidTokenMatch() throws Exception {
        String ErrorMessage = "provided token does not match the creator in game with gameID: " + game.getGameID();
        Throwable response = new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorMessage);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        Mockito.doThrow(response).when(gameService).start(1L, user.getToken());
        
        MockHttpServletRequestBuilder postRequest = post("/games/" + game.getGameID() + "/start")
            .header("token", user.getToken())
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*");

        mockMvc.perform(postRequest)
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllGamesValid() throws Exception {
        List<GameData> allGames = Collections.singletonList(game.status());

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        given(gameService.getAllGames()).willReturn(allGames);

        MockHttpServletRequestBuilder getRequest = get("/games")
            .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].gameID", is(1)))
                .andExpect(jsonPath("$[0].status", is("LOBBY")))
                .andExpect(jsonPath("$[0].name", is(gamePostDTO.getName())))
                .andExpect(jsonPath("$[0].typeOfGame", is(gamePostDTO.getTypeOfGame())))
                .andExpect(jsonPath("$[0].powerupsActive", is(false)))
                .andExpect(jsonPath("$[0].eventsActive", is(false)))
                .andExpect(jsonPath("$[0].timer", is(0)))
                .andExpect(jsonPath("$[0].totalLobbySize", is(gamePostDTO.getTotalLobbySize())))
                .andExpect(jsonPath("$[0].numberOfPlayersInLobby", is(1)))
                .andExpect(jsonPath("$[0].numberOfRoundsToPlay", is(gamePostDTO.getNumberOfRoundsToPlay())))
                .andExpect(jsonPath("$[0].currentRoundPlayed", is(0)))
                .andExpect(jsonPath("$[0].event", org.hamcrest.CoreMatchers.nullValue()))
                .andExpect(jsonPath("$[0].creator", is(gamePostDTO.getCreator())));
    }

    @Test
    void getAllGamesInvalidFilter() throws Exception {
        String ErrorMessage = "invalid filter argument provided";
        Throwable response = new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        given(gameService.getAllGames()).willThrow(response);

        MockHttpServletRequestBuilder getRequest = get("/games?nam=eee")
            .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isConflict());
    }

    @Test
    void getAllGamesInvalidToken() throws Exception {
        Throwable response = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The token (" + user.getToken() + ")  " + "is invalid");

        Mockito.doThrow(response).when(userService).checkToken(user.getToken());

        MockHttpServletRequestBuilder getRequest = get("/games")
            .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }

    @Test
    void getPlayersValid() throws Exception {
        Player player = new Player(user);
        player.setState(PlayerState.ACTIVE);
        PlayerData playerData = player.status();

        List<Player> players = Collections.singletonList(player);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        Mockito.doNothing().when(gameService).tokenMatch(user.getToken(), game.getGameID());
        given(gameService.players(game.getGameID())).willReturn(players);

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameID() + "/players")
            .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(playerData.getUsername())))
                .andExpect(jsonPath("$[0].playerID", is(playerData.getPlayerID())))
                .andExpect(jsonPath("$[0].accountBalance", is(playerData.getAccountBalance())))
                .andExpect(jsonPath("$[0].numberOfWonRounds", is(playerData.getNumberOfWonRounds())))
                .andExpect(jsonPath("$[0].numberOfLostRounds", is(playerData.getNumberOfLostRounds())))
                .andExpect(jsonPath("$[0].typeOfCurrentBet", is(playerData.getTypeOfCurrentBet().toString())));
    }

    @Test
    void getPlayersInvalidTokenMatch() throws Exception {
        String ErrorMessage = "provided token does not match the creator in game with gameID: " + game.getGameID();
        Throwable response = new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorMessage);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        Mockito.doThrow(response).when(gameService).tokenMatch(user.getToken(), game.getGameID());

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameID() + "/players")
            .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getChartValid() throws Exception {
        List<Double> numbers = Arrays.asList(1.1, 1.2);
        List<String> dates = Arrays.asList("2000-01-01 01:01:01", "2000-01-02 01:01:01");

        ChartData chartData = new ChartData();
        chartData.setFromCurrency(Currency.CHF);
        chartData.setToCurrency(Currency.EUR);
        chartData.setNumbers(numbers);
        chartData.setDates(dates);

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        Mockito.doNothing().when(gameService).tokenMatch(user.getToken(), game.getGameID());
        given(gameService.chart(game.getGameID())).willReturn(chartData);

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameID() + "/chart")
            .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCurrency", is(chartData.getFromCurrency().toString())))
                .andExpect(jsonPath("$.toCurrency", is(chartData.getToCurrency().toString())))
                .andExpect(jsonPath("$.dates", is(chartData.getDates())))
                .andExpect(jsonPath("$.numbers", is(chartData.getNumbers())));
    }

    @Test
    void getChartInvalidStatus() throws Exception {
        Throwable response = new ResponseStatusException(HttpStatus.CONFLICT, "Not in valid state.");

        Mockito.doNothing().when(userService).checkToken(user.getToken());
        Mockito.doNothing().when(gameService).tokenMatch(user.getToken(), game.getGameID());
        given(gameService.chart(game.getGameID())).willThrow(response);

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameID() + "/chart")
            .header("token", user.getToken());

        mockMvc.perform(getRequest)
                .andExpect(status().isConflict());
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
