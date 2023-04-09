package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.Betting.Bet;
import ch.uzh.ifi.hase.soprafs23.Betting.Result;
import ch.uzh.ifi.hase.soprafs23.Data.PlayerData;
import ch.uzh.ifi.hase.soprafs23.Runner.GameRunner;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.BetPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.ResultGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class PlayerController {
    private final GameService gameService;
    private final UserService userService;

    private final PlayerService playerService;

    public PlayerController(GameService gameService, UserService userService, PlayerService playerService) {
        this.gameService = gameService;
        this.userService = userService;
        this.playerService = playerService;
    }

    @GetMapping("/players/{playerID}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @CrossOrigin
    public PlayerGetDTO getPlayerByPlayerID(@PathVariable("playerID") Long playerID, @RequestHeader("token") String token) {
        //returns a user for a provided userID
        this.userService.checkToken(token);

        Player player = playerService.getPlayerByPlayerID(playerID);
        PlayerData playerData = player.status();

        return DTOMapper.INSTANCE.convertPlayerDataToPlayerGetDTO(playerData);
    }

    @PutMapping("/players/{playerID}/bet")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    @CrossOrigin
    public void placeBet(@RequestBody BetPutDTO betPutDTO,  @PathVariable("playerID") Long playerID, @RequestHeader("token") String token) {
        //returns a user for a provided userID
        this.userService.checkToken(token);

        Bet betToPlace = DTOMapper.INSTANCE.convertBetPutDTOToBet(betPutDTO);
        this.playerService.placeBet(betToPlace, playerID);
    }

    @GetMapping("/players/{playerID}/result")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @CrossOrigin
    public ResultGetDTO result(@PathVariable("playerID") Long playerID, @RequestHeader("token") String token) {
        //returns a user for a provided userID
        this.userService.checkToken(token);

        Result result = this.playerService.getResult(playerID);

        return DTOMapper.INSTANCE.convertResultToResultGetDTO(result);
    }
}
