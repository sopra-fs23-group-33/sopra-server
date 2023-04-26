package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.min;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) {
        newUser.setUsername(newUser.getUsername().trim()); // trim: why have spaces before and after? may ruin username display
        this.checkIfValidUser(newUser);
        
        User userCreated = new User(newUser.getUsername(), newUser.getPassword());
        int rank = this.getUsers().size() + 1;
        userCreated.setRank(rank);
        userCreated = this.userRepository.save(userCreated);
        this.userRepository.flush();
        log.debug("Created Information for User: {}", newUser);
        return userCreated;
    }

    private void checkIfValidUser(User userToBeCreated) {

        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        if (userByUsername != null) {
            String errorMessage = "add User failed because username already exists";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

        String errorMessage = checkIfValidUsername(userToBeCreated.getUsername()) + checkIfValidPassword(userToBeCreated.getPassword());
        if (!errorMessage.equals("")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage.substring(0, errorMessage.length() - 2));
        }
    }

    private String checkIfValidUsername(String username){
        Pattern patternOneLetter = Pattern.compile("[a-zA-Z]");
        Pattern patternInvalidCharacters = Pattern.compile("[^a-zA-Z0-9_!?#@&$.]");

        Matcher matcherOneLetter = patternOneLetter.matcher(username);
        Matcher matcherInvalidCharacters = patternInvalidCharacters.matcher(username);

        String errorMessage = "";

        if (!matcherOneLetter.find()){
            errorMessage = errorMessage + "Invalid username: Does not contain alphabetic characters.\n";
        } 
        
        if (matcherInvalidCharacters.find()){
            errorMessage = errorMessage +  "Invalid username: Contains invalid characters.\n";
        } 
        
        if (username.length() > 30){
            errorMessage = errorMessage +  "Invalid username: Too long.\n";
        }

        return errorMessage;
    }

    private String checkIfValidPassword(String password){
        Pattern patternOneLetter = Pattern.compile("[a-zA-Z]");
        Pattern patternOneNumber = Pattern.compile("\\d");
        Pattern patternOneSpecial = Pattern.compile("[_?!#@&$.]");
        Pattern patternInvalidCharacters = Pattern.compile("[^a-zA-Z0-9_?!#@&$.]");

        Matcher matcherOneLetter = patternOneLetter.matcher(password);
        Matcher matcherOneNumber = patternOneNumber.matcher(password);
        Matcher matcherOneSpecial = patternOneSpecial.matcher(password);
        Matcher matcherInvalidCharacters = patternInvalidCharacters.matcher(password);

        String errorMessage = "";

        if (!matcherOneLetter.find()){
            errorMessage = errorMessage + "Invalid password: Does not contain alphabetic characters.\n";
        } 
        
        if (!matcherOneNumber.find()){
            errorMessage = errorMessage +  "Invalid password: Does not contain numeric characters.\n";
        } 
        
        if (!matcherOneSpecial.find()){
            errorMessage = errorMessage +  "Invalid password: Does not contain special characters.\n";
        } 
        
        if (matcherInvalidCharacters.find()){
            errorMessage = errorMessage +  "Invalid password: Contains invalid characters.\n";
        } 
        
        if (password.length() < 8){
            errorMessage = errorMessage +  "Invalid password: Too short.\n";
        } else if (password.length() > 30){
            errorMessage = errorMessage +  "Invalid password: Too long.\n";
        }

        return errorMessage;
    }

    public User getUserByUsername(String username) {
        User userByUsername = this.userRepository.findByUsername(username);
        if (userByUsername != null)
            return userByUsername;

        else {
            String errorMessage = "User with username " + username + " was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }

    }

    public void checkToken(String token) {
        String test = "test123";
        if (token.equals(test))
            return;

        User userByToken = this.userRepository.findByToken(token);

        if (userByToken == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The token (" + token + ")  " + "is invalid");
    }

    public User getUserByUserID(Long userID) {
        User userByID = this.userRepository.findByUserID(userID);

        if (userByID != null)
            return userByID;
        else {
            String errorMessage = "User with userId " + userID + " was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }
    }

    public User loginUser(User userToLogin) {
        User foundUser = this.getUserByUsername(userToLogin.getUsername());

        if (!foundUser.getPassword().equals(userToLogin.getPassword())) {
            String errorMessage = "login failed because password is wrong";
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);
        }
        /*
        else if (foundUser.getStatus().equals(UserStatus.PLAYING)) {
            String errorMessage = "login failed because user is currently playing a game";
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);
        }
        */
        foundUser.setStatus(UserStatus.ONLINE);
        foundUser.setToken(UUID.randomUUID().toString());
        foundUser = this.userRepository.saveAndFlush(foundUser);
        log.debug("Created Information for User: {}", foundUser);
        return foundUser;
    }

    public void logoutUser(Long userID) {
        User userByID = getUserByUserID(userID);

        if (userByID.getStatus().equals(UserStatus.OFFLINE)) {
            String errorMessage = "logout failed because user was not logged in";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

        else {
            userByID.setStatus(UserStatus.OFFLINE);
            this.userRepository.saveAndFlush(userByID);
        }
    }

    public List<User> leaderboard() {
        this.updateRanks();
        List<User> users = this.getUsers();

        users.sort(Comparator.comparingDouble(User::getWinRate).reversed().thenComparing(User::getUserID));

        return users.subList(0, min(users.size(), 9));
    }
    private void updateRanks(){
        List<User> users = this.getUsers();

        users.sort(Comparator.comparingDouble(User::getWinRate).reversed().thenComparing(User::getUserID));

        int rank = 1;

        for(User user: users){
            user.setRank(rank);
            rank++;
            this.userRepository.save(user);
        }

        this.userRepository.flush();
    }
}
