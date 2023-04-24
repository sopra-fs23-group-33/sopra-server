package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.Betting.Bet;
import ch.uzh.ifi.hase.soprafs23.Betting.Result;
import ch.uzh.ifi.hase.soprafs23.Data.ChartData;
import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Data.PlayerData;
import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Forex.CurrencyPair;

import ch.uzh.ifi.hase.soprafs23.constant.*;

import ch.uzh.ifi.hase.soprafs23.entity.User;

import ch.uzh.ifi.hase.soprafs23.rest.dto.*;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */

@WebAppConfiguration
@SpringBootTest
@Transactional
class DTOMapperTest {

    @Test
    void testCreateUser_fromUserPostDTO_toUser_success() {
        // create UserPostDTO
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("name");
        userPostDTO.setUsername("username");

        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // check content
        assertEquals(userPostDTO.getPassword(), user.getPassword());
        assertEquals(userPostDTO.getUsername(), user.getUsername());
    }

    @Test
    void testGetUser_fromUser_toUserGetDTO_success() {
        // create User
        User user = new User();
        user.setUserID(12321L);
        user.setUsername("firstname@lastname");
        user.setToken("token1");
        user.setCreationDate(LocalDate.parse("2023-04-01"));
        user.setTotalRoundsPlayed(0);
        user.setNumberOfBetsWon(0);
        user.setNumberOfBetsLost(0);
        user.setRank(-1);
        user.setStatus(UserStatus.OFFLINE);

        // MAP -> Create UserGetDTO
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        // check content
        assertEquals(user.getUserID(), userGetDTO.getUserID());
        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getStatus(), userGetDTO.getStatus());
        assertEquals(user.getToken(), userGetDTO.getToken());
        assertEquals(user.getCreationDate(), userGetDTO.getCreationDate());
        assertEquals(user.getTotalRoundsPlayed(), userGetDTO.getTotalRoundsPlayed());
        assertEquals(user.getNumberOfBetsLost(), userGetDTO.getNumberOfBetsLost());
        assertEquals(user.getNumberOfBetsWon(), userGetDTO.getNumberOfBetsWon());
        assertEquals(user.getRank(), userGetDTO.getRank());
    }

    @Test
    void convertChartDataToChartGetDTO(){
        ArrayList<String> dates = new ArrayList<>();
        dates.add("Date");

        ArrayList<Double> numbers = new ArrayList<>();
        numbers.add(1.0);

        Chart chart = new Chart(numbers,dates, new CurrencyPair(Currency.CHF, Currency.EUR));
        ChartData chartData = chart.status();

        ChartGetDTO chartGetDTO= DTOMapper.INSTANCE.convertChartDataToChartGetDTO(chartData);

        assertEquals(chartGetDTO.getDates(), chartData.getDates());
        assertEquals(chartGetDTO.getNumbers(), chartData.getNumbers());
        assertEquals(chartGetDTO.getFromCurrency(), chartData.getFromCurrency());
        assertEquals(chartGetDTO.getToCurrency(), chartData.getToCurrency());
    }

    @Test
    void convertResultToResultGetDTO(){
        Result result = new Result(Direction.UP, 200, 1200);

        ResultGetDTO resultGetDTO= DTOMapper.INSTANCE.convertResultToResultGetDTO(result);

        assertEquals(resultGetDTO.getBettingAmount(), result.getBettingAmount());
        assertEquals(resultGetDTO.getOutcome(), result.getOutcome());
        assertEquals(resultGetDTO.getProfit(), result.getProfit());
    }

    @Test
    void convertBetPutDTOToBet(){
        BetPutDTO betPutDTO= new BetPutDTO();
        betPutDTO.setAmount(100);
        betPutDTO.setType(Direction.DOWN);

        Bet bet = DTOMapper.INSTANCE.convertBetPutDTOToBet(betPutDTO);

        assertEquals(bet.getDirection(), betPutDTO.getType());
        assertEquals(bet.getAmount(),betPutDTO.getAmount());
    }

    @Test
    void convertPlayerDataToPlayerGetDTO(){

        PlayerData playerData = new PlayerData();
        playerData.setUsername("test");
        playerData.setPlayerID(1L);
        playerData.setNumberOfLostRounds(1);
        playerData.setTypeOfCurrentBet(Direction.DOWN);
        playerData.setNumberOfWonRounds(2);

        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertPlayerDataToPlayerGetDTO(playerData);

        assertEquals(playerGetDTO.getAccountBalance(), playerData.getAccountBalance());
        assertEquals(playerGetDTO.getPlayerID(), playerData.getPlayerID());
        assertEquals(playerGetDTO.getNumberOfLostRounds(), playerData.getNumberOfLostRounds());
        assertEquals(playerGetDTO.getNumberOfWonRounds(), playerData.getNumberOfWonRounds());
        assertEquals(playerGetDTO.getTypeOfCurrentBet(), playerData.getTypeOfCurrentBet().toString());
        assertEquals(playerGetDTO.getUsername(), playerData.getUsername());
    }

    @Test
    void convertGamePostDTOToGameData(){
        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setCreator("Test");
        gamePostDTO.setTypeOfGame("MULTIPLAYER");
        gamePostDTO.setName("MyGame");
        gamePostDTO.setEventsActive(false);
        gamePostDTO.setPowerupsActive(false);
        gamePostDTO.setTotalLobbySize(3);
        gamePostDTO.setNumberOfRoundsToPlay(3);

        GameData gameData = DTOMapper.INSTANCE.convertGamePostDTOToGameData(gamePostDTO);

        assertEquals(gameData.getCreator(), gamePostDTO.getCreator());
        assertEquals(gameData.getTypeOfGame().toString(), gamePostDTO.getTypeOfGame());
        assertEquals(gameData.getName(), gamePostDTO.getName());
        assertEquals(gameData.getNumberOfRoundsToPlay(), gamePostDTO.getNumberOfRoundsToPlay());
        assertEquals(gameData.getTotalLobbySize(), gamePostDTO.getTotalLobbySize());
        assertEquals(gameData.isEventsActive(), gamePostDTO.isEventsActive());
        assertEquals(gameData.isPowerupsActive(), gamePostDTO.isPowerupsActive());

    }

    @Test
    void convertGameDataToGameGetDTO(){
        GameData gameData = new GameData();
        gameData.setNumberOfRoundsToPlay(3);
        gameData.setTypeOfGame(GameType.MULTIPLAYER);
        gameData.setPowerupsActive(false);
        gameData.setEventsActive(false);
        gameData.setName("MyGame");
        gameData.setTotalLobbySize(2);
        gameData.setGameID(1L);
        gameData.setEvent("NO_EVENT");
        gameData.setCreator("Creator");
        gameData.setTimer(10);
        gameData.setCurrentRoundPlayed(2);
        gameData.setNumberOfPlayersInLobby(1);
        gameData.setStatus(GameState.LOBBY);

        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertGameDataToGameGetDTO(gameData);

        assertEquals(gameData.getCreator(), gameGetDTO.getCreator());
        assertEquals(gameData.getTypeOfGame().toString(), gameGetDTO.getTypeOfGame());
        assertEquals(gameData.getName(), gameGetDTO.getName());
        assertEquals(gameData.getNumberOfRoundsToPlay(), gameGetDTO.getNumberOfRoundsToPlay());
        assertEquals(gameData.getTotalLobbySize(), gameGetDTO.getTotalLobbySize());
        assertEquals(gameData.isEventsActive(), gameGetDTO.isEventsActive());
        assertEquals(gameData.isPowerupsActive(), gameGetDTO.isPowerupsActive());
        assertEquals(gameData.getEvent(), gameGetDTO.getEvent());
        assertEquals(gameData.getCreator(), gameGetDTO.getCreator());
        assertEquals(gameData.getTimer(), gameGetDTO.getTimer());
        assertEquals(gameData.getCurrentRoundPlayed(), gameGetDTO.getCurrentRoundPlayed());
        assertEquals(gameData.getNumberOfPlayersInLobby(), gameGetDTO.getNumberOfPlayersInLobby());
        assertEquals(gameData.getStatus().toString(), gameGetDTO.getStatus());
        assertEquals(gameData.getGameID(), gameGetDTO.getGameID());
    }
}
