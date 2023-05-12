package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.Data.ChartData;
import ch.uzh.ifi.hase.soprafs23.Data.EventData;
import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Data.PlayerData;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents.AbstractPowerUp;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;

import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

@RestController
public class GameController {
    private final GameService gameService;
    private final UserService userService;

    GameController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }


    @PostMapping("/games/create")
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

    @GetMapping("/games/{gameID}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @CrossOrigin
    public GameGetDTO getGameByID(@PathVariable("gameID") Long gameID, @RequestHeader("token") String token) {
        this.userService.checkToken(token);

        Game game = this.gameService.getGameByGameID(gameID);

        GameData gameData = game.status();

        return DTOMapper.INSTANCE.convertGameDataToGameGetDTO(gameData);
    }

    @GetMapping("/games/{gameID}/creator")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @CrossOrigin
    public PlayerGetDTO getCreator(@PathVariable("gameID") Long gameID, @RequestHeader("token") String token) {
        this.userService.checkToken(token);

        Player creator = this.gameService.creator(gameID);
        PlayerData playerData = creator.status();

        return DTOMapper.INSTANCE.convertPlayerDataToPlayerGetDTO(playerData);
    }

    @GetMapping("/games/{gameID}/status")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @CrossOrigin
    public GameGetDTO getStatus(@PathVariable("gameID") Long gameID, @RequestHeader("token") String token) {
        this.userService.checkToken(token);

        Game game = this.gameService.getGameByGameID(gameID);

        GameData gameData = game.status();

        return DTOMapper.INSTANCE.convertGameDataToGameGetDTO(gameData);
    }

    @PostMapping("/games/{gameID}/join")
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

    @PostMapping("/games/{gameID}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CrossOrigin
    public void leave(@PathVariable("gameID") Long gameID, @RequestBody UserPostDTO userPostDTO, @RequestHeader("token") String token) {
        this.userService.checkToken(token);
        this.gameService.tokenMatch(token, gameID);

        User userToLeave = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        this.gameService.leave(userToLeave, gameID);
    }

    @PostMapping("/games/{gameID}/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CrossOrigin
    public void start(@PathVariable("gameID") Long gameID, @RequestHeader("token") String token)  {
        this.userService.checkToken(token);

        this.gameService.start(gameID, token);
    }

    @GetMapping("/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameGetDTO> getAllGames(@RequestParam(required = false) String filter, @RequestHeader("token") String token) {

        this.userService.checkToken(token);

        List<GameData> games;

        if(filter != null){
            games = this.gameService.getAllGames(filter);
        }

        else{
            games = this.gameService.getAllGames();
        }

        List<GameGetDTO> gameGetDTOS = new ArrayList<>();

        // convert each user to the API representation
        for (GameData gameData : games) {
            gameGetDTOS.add(DTOMapper.INSTANCE.convertGameDataToGameGetDTO(gameData));
        }
        return gameGetDTOS;
    }

    @GetMapping("/games/{gameID}/players")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin
    public List<PlayerGetDTO> players(@PathVariable("gameID") Long gameID, @RequestHeader("token") String token)  {
        this.userService.checkToken(token);
        this.gameService.tokenMatch(token, gameID);

        List<Player> players = this.gameService.players(gameID);

        List<PlayerGetDTO> playerGetDTOS = new ArrayList<>();

        for(Player player: players){
            playerGetDTOS.add(DTOMapper.INSTANCE.convertPlayerDataToPlayerGetDTO(player.status()));
        }

        return playerGetDTOS;
    }

    @GetMapping("/games/{gameID}/chart")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin
    public ChartGetDTO chart(@PathVariable("gameID") Long gameID, @RequestHeader("token") String token)  {
        this.userService.checkToken(token);
        this.gameService.tokenMatch(token, gameID);

        ChartData chartData = this.gameService.chart(gameID);

        return DTOMapper.INSTANCE.convertChartDataToChartGetDTO(chartData);
    }

    @GetMapping("/games/{gameID}/powerups")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin
    public List<PowerupGetDTO> usedPowerups(@PathVariable("gameID") Long gameID, @RequestHeader("token") String token)  {
        this.userService.checkToken(token);
        this.gameService.tokenMatch(token, gameID);

        List<AbstractPowerUp> usedPowerups = this.gameService.getUsedPowerups(gameID);

        List<PowerupGetDTO> powerupGetDTOS = new ArrayList<>();

        for(AbstractPowerUp powerup: usedPowerups){
            powerupGetDTOS.add(DTOMapper.INSTANCE.convertAbstractPowerupToPowerupGetDTO(powerup));
        }

        return powerupGetDTOS;
    }

    @GetMapping("/games/{gameID}/event")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin
    public EventGetDTO event(@PathVariable("gameID") Long gameID, @RequestHeader("token") String token)  {
        this.userService.checkToken(token);
        this.gameService.tokenMatch(token, gameID);

        EventData eventData = this.gameService.getEvent(gameID);

        return DTOMapper.INSTANCE.convertEventDataToEventGetDTO(eventData);
    }

}
