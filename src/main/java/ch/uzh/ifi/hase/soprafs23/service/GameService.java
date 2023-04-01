package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.constant.UserState;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class GameService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final GameRepository gameRepository;
    private final UserService userService;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository, UserService userService) {
        this.gameRepository = gameRepository;
        this.userService = userService;
    }


    public Game createGame(User user){
        if(!user.getState().equals(UserState.ONLINE)) {
            String ErrorMessage = "cannot Create game because user is still in an ongoing game";
            throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
        }
        User creator = this.userService.getUserByUserID(user.getUserID());

        Game newGame = new Game(creator, "Test", 10,10, false,false, GameType.MULTIPLAYER);

        Game createdGame = this.gameRepository.saveAndFlush(newGame);
        return createdGame;
    }


}
