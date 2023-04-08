package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Data.PlayerData;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.Runner.GameRunner;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameController {
    private final GameService gameService;
    private final UserService userService;

    private final GameRunner gameRunner;

    GameController(GameService gameService, UserService userService, GameRunner gameRunner) {
        this.gameService = gameService;
        this.userService = userService;
        this.gameRunner = gameRunner;
    }


    @PostMapping("/game/create")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGetDTO createGame(@RequestBody GamePostDTO gamePostDTO, @RequestHeader("token") String token) {
        // convert API user to internal representation
        this.userService.checkToken(token);
        User creator = this.userService.getUserByUsername(gamePostDTO.getCreator());
        GameData gameData = DTOMapper.INSTANCE.convertGamePostDTOToGameData(gamePostDTO);
        Game game = this.gameService.createGame(creator, gameData);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertGameDataToGameGetDTO(game.status());
    }

    @GetMapping("/game/{gameID}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @CrossOrigin
    public GameGetDTO getGameByID(@PathVariable("gameID") Long gameID, @RequestHeader("token") String token) {
        this.userService.checkToken(token);

        Game game = this.gameService.getGameByGameID(gameID);

        GameData gameData = game.status();

        return DTOMapper.INSTANCE.convertGameDataToGameGetDTO(gameData);
    }

    @GetMapping("/game/{gameID}/creator")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @CrossOrigin
    public PlayerGetDTO getCreator(@PathVariable("gameID") Long gameID, @RequestHeader("token") String token) {
        this.userService.checkToken(token);

        Game game = this.gameService.getGameByGameID(gameID);
        Player creator = game.creator();
        PlayerData playerData = creator.status();

        return DTOMapper.INSTANCE.convertPlayerDataToPlayerGetDTO(playerData);
    }

    @GetMapping("/game/{gameID}/status")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @CrossOrigin
    public GameGetDTO getStatus(@PathVariable("gameID") Long gameID, @RequestHeader("token") String token) {
        this.userService.checkToken(token);

        Game game = this.gameService.getGameByGameID(gameID);

        GameData gameData = game.status();

        return DTOMapper.INSTANCE.convertGameDataToGameGetDTO(gameData);
    }

    @PostMapping("/game/{gameID}/join")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @CrossOrigin
    public PlayerGetDTO join(@PathVariable("gameID") Long gameID, @RequestBody UserPostDTO userPostDTO, @RequestHeader("token") String token) {
        this.userService.checkToken(token);

        User userToJoin = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        Player player = this.gameService.join(userToJoin, gameID);
        PlayerData playerData = player.status();

        return DTOMapper.INSTANCE.convertPlayerDataToPlayerGetDTO(playerData);
    }

    @PostMapping("/game/{gameID}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CrossOrigin
    public void leave(@PathVariable("gameID") Long gameID, @RequestBody UserPostDTO userPostDTO, @RequestHeader("token") String token) {
        this.userService.checkToken(token);

        User userToLeave = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        this.gameService.leave(userToLeave, gameID);
    }

    @PostMapping("/game/{gameID}/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CrossOrigin
    public void start(@PathVariable("gameID") Long gameID, @RequestHeader("token") String token)  {
        this.userService.checkToken(token);

        this.gameService.start(gameID);
    }

}
