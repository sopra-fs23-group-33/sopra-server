package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.Forex.ChartAPI;
import ch.uzh.ifi.hase.soprafs23.Forex.CurrencyPair;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
//import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.constant.Currency;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.constant.UserState;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 * Test of a push on Branch Stefan
 */
@RestController
public class TestController {

  private final UserService userService;
  private final UserRepository userRepository;

  //private final GameRunner gameRunner;

  private final GameRepository gameRepository;

  private int count = 0;


  private GameRound gameRound;

  @Autowired
  TestController(UserService userService, UserRepository gameRoundRepository, GameRepository gameRepository) {
    this.userService = userService;
    this.userRepository = gameRoundRepository;
      this.gameRepository = gameRepository;
  }



  @GetMapping("/api")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public GameRound test_api(@PathVariable("id") Long id) throws InterruptedException {
      ChartAPI api = new ChartAPI();
      GameRound gr = api.getGameRound(new CurrencyPair(Currency.CHF, Currency.EUR));
      return gr;
  }




}


