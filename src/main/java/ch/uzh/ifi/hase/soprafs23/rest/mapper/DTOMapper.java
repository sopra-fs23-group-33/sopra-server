package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.Betting.Bet;
import ch.uzh.ifi.hase.soprafs23.Betting.Result;
import ch.uzh.ifi.hase.soprafs23.Data.ChartData;
import ch.uzh.ifi.hase.soprafs23.Data.EventData;
import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Data.PlayerData;
import ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents.AbstractPowerUp;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "userID", target = "userID")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "token", target = "token")
  @Mapping(source = "creationDate", target = "creationDate")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "totalRoundsPlayed", target = "totalRoundsPlayed")
  @Mapping(source = "numberOfBetsWon", target = "numberOfBetsWon")
  @Mapping(source = "numberOfBetsLost", target = "numberOfBetsLost")
  @Mapping(source = "winRate", target = "winRate")
  @Mapping(source = "profit", target = "profit")
  UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "gameID", target = "gameID")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "typeOfGame", target = "typeOfGame")
    @Mapping(source = "totalLobbySize", target = "totalLobbySize")
    @Mapping(source = "numberOfPlayersInLobby", target = "numberOfPlayersInLobby")
    @Mapping(source = "numberOfRoundsToPlay", target = "numberOfRoundsToPlay")
    @Mapping(source = "currentRoundPlayed", target = "currentRoundPlayed")
    @Mapping(source = "powerupsActive", target = "powerupsActive")
    @Mapping(source = "eventsActive", target = "eventsActive")
    @Mapping(source = "timer", target = "timer")
    @Mapping(source = "creator", target = "creator")
    GameGetDTO convertGameDataToGameGetDTO(GameData gameData);



    @Mapping(source = "name", target = "name")
    @Mapping(source = "typeOfGame", target = "typeOfGame")
    @Mapping(source = "totalLobbySize", target = "totalLobbySize")
    @Mapping(source = "numberOfRoundsToPlay", target = "numberOfRoundsToPlay")
    @Mapping(source = "powerupsActive", target = "powerupsActive")
    @Mapping(source = "eventsActive", target = "eventsActive")
    @Mapping(source = "creator", target = "creator")
    GameData convertGamePostDTOToGameData(GamePostDTO gamePostDTO);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "playerID", target = "playerID")
    @Mapping(source = "accountBalance", target = "accountBalance")
    @Mapping(source = "numberOfWonRounds", target = "numberOfWonRounds")
    @Mapping(source = "numberOfLostRounds", target = "numberOfLostRounds")
    @Mapping(source = "typeOfCurrentBet", target = "typeOfCurrentBet")
    PlayerGetDTO convertPlayerDataToPlayerGetDTO(PlayerData playerData);

    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "type", target = "direction")
    Bet convertBetPutDTOToBet(BetPutDTO betPutDTO);


    @Mapping(source = "outcome", target = "outcome")
    @Mapping(source = "bettingAmount", target = "bettingAmount")
    @Mapping(source = "profit", target = "profit")
    ResultGetDTO convertResultToResultGetDTO(Result result);

    @Mapping(source = "numbers", target = "numbers")
    @Mapping(source = "dates", target = "dates")
    @Mapping(source = "fromCurrency", target = "fromCurrency")
    @Mapping(source = "toCurrency", target = "toCurrency")
    ChartGetDTO convertChartDataToChartGetDTO(ChartData chartData);

    @Mapping(source = "ownerID", target = "ownerID")
    @Mapping(source = "ownerName", target = "ownerName")
    @Mapping(source = "powerupID", target = "powerupID")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "powerupType", target = "powerupType")
    @Mapping(source = "name", target = "name")
    PowerupGetDTO convertAbstractPowerupToPowerupGetDTO(AbstractPowerUp abstractPowerUp);

    @Mapping(source = "description", target = "description")
    @Mapping(source = "name", target = "name")
    EventGetDTO convertEventDataToEventGetDTO(EventData eventData);
}
