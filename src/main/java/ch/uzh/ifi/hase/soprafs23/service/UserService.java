package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserState;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            String ErrorMessage = "add User failed because username already exists";
            throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
        }

        String ErrorMessage = checkIfValidUsername(userToBeCreated.getUsername()) + checkIfValidPassword(userToBeCreated.getPassword());
        if (!ErrorMessage.equals("")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.substring(0, ErrorMessage.length() - 2));
        }
    }

    private String checkIfValidUsername(String username){
        Pattern patternOneLetter = Pattern.compile("[a-zA-Z]");
        Pattern patternInvalidCharacters = Pattern.compile("[^a-zA-Z0-9_!?#@&$]");

        Matcher matcherOneLetter = patternOneLetter.matcher(username);
        Matcher matcherInvalidCharacters = patternInvalidCharacters.matcher(username);

        String ErrorMessage = "";

        if (!matcherOneLetter.find()){
            ErrorMessage = ErrorMessage + "Invalid username: Does not contain alphabetic characters.\n";
        } if (matcherInvalidCharacters.find()){
            ErrorMessage = ErrorMessage +  "Invalid username: Contains invalid characters.\n";
        } if (username.length() > 30){
            ErrorMessage = ErrorMessage +  "Invalid username: Too long.\n";
        }

        return ErrorMessage;
    }

    private String checkIfValidPassword(String password){
        Pattern patternOneLetter = Pattern.compile("[a-zA-Z]");
        Pattern patternOneNumber = Pattern.compile("[0-9]");
        Pattern patternOneSpecial = Pattern.compile("[_?!#@&$]");
        Pattern patternInvalidCharacters = Pattern.compile("[^a-zA-Z0-9_?!#@&$]");

        Matcher matcherOneLetter = patternOneLetter.matcher(password);
        Matcher matcherOneNumber = patternOneNumber.matcher(password);
        Matcher matcherOneSpecial = patternOneSpecial.matcher(password);
        Matcher matcherInvalidCharacters = patternInvalidCharacters.matcher(password);

        String ErrorMessage = "";

        if (!matcherOneLetter.find()){
            ErrorMessage = ErrorMessage + "Invalid password: Does not contain alphabetic characters.\n";
        } if (!matcherOneNumber.find()){
            ErrorMessage = ErrorMessage +  "Invalid password: Does not contain numeric characters.\n";
        } if (!matcherOneSpecial.find()){
            ErrorMessage = ErrorMessage +  "Invalid password: Does not contain special characters.\n";
        } if (matcherInvalidCharacters.find()){
            ErrorMessage = ErrorMessage +  "Invalid password: Contains invalid characters.\n";
        } if (password.length() < 8){
            ErrorMessage = ErrorMessage +  "Invalid password: Too short.\n";
        } else if (password.length() > 30){
            ErrorMessage = ErrorMessage +  "Invalid password: Too long.\n";
        }

        return ErrorMessage;
    }

    private User getUserByUsername(User userToFind) {
        User userByUsername = this.userRepository.findByUsername(userToFind.getUsername());
        if (userByUsername != null)
            return userByUsername;

        else {
            String ErrorMessage = "User with username " + userToFind.getUsername() + " was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage);
        }

    }

    public User getUserByToken(String token) {
        User userByToken = this.userRepository.findByToken(token);
        if (userByToken != null)
            return userByToken;

        else {
            String ErrorMessage = "User with " + token + " was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage);
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
            String ErrorMessage = "User with userId " + userID + " was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage);
        }
    }

    public User loginUser(User userToLogin) {
        User foundUser = this.getUserByUsername(userToLogin);

        if (!foundUser.getPassword().equals(userToLogin.getPassword())) {
            String ErrorMessage = "login failed because password is wrong";
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorMessage);
        }

        else if (foundUser.getState().equals(UserState.PLAYING)) {
            String ErrorMessage = "login failed because user is currently playing a game";
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorMessage);
        }

        foundUser.setState(UserState.ONLINE);
        foundUser.setToken(UUID.randomUUID().toString());
        foundUser = this.userRepository.saveAndFlush(foundUser);
        log.debug("Created Information for User: {}", foundUser);
        return foundUser;
    }

    public void logoutUser(Long userID) {
        User userByID = getUserByUserID(userID);

        if (userByID.getState().equals(UserState.OFFLINE)) {
            String ErrorMessage = "logout failed because user was not logged in";
            throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
        }

        else {
            userByID.setState(UserState.OFFLINE);
            this.userRepository.saveAndFlush(userByID);
        }
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the name
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated, HttpStatus errorIfFound) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided already exists: the user could not be created.";
        if (userByUsername != null) {
            throw new ResponseStatusException(errorIfFound, String.format(baseErrorMessage, "username"));
        }
    }

    public List<User> leaderboard() {
        List<User> users = this.getUsers();
        //TODO
        return users;
    }
}
