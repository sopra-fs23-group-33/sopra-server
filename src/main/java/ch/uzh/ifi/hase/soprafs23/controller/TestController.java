package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.Data.ChartData;
import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Forex.ChartAPI;
import ch.uzh.ifi.hase.soprafs23.Forex.CurrencyPair;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;

import ch.uzh.ifi.hase.soprafs23.Runner.BackgroundChartFetcher;
import ch.uzh.ifi.hase.soprafs23.constant.Currency;

import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRoundRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.ChartGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;

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

    private  ArrayList<Integer> integers;

  private final UserService userService;
  private final UserRepository userRepository;
  private final GameRepository gameRepository;
  private final GameRoundRepository gameRoundRepository;

  private int count = 0;

  private GameRound gameRound;

  @Autowired
  BackgroundChartFetcher backgroundChartFetcher;

  @Autowired
  TestController(UserService userService, UserRepository userRepository, GameRepository gameRepository,  GameRoundRepository gameRoundRepository) {
    this.userService = userService;
    this.userRepository = userRepository;
      this.gameRepository = gameRepository;
      this.integers = new ArrayList<>();
      this.gameRoundRepository = gameRoundRepository;
  }



  //this class/ controller is used for debugging and testing for developers. Add mappings as you see fit!


}


