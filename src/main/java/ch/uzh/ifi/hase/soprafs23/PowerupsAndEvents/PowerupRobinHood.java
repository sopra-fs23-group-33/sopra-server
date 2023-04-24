package ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.round;

@Entity
public class PowerupRobinHood extends  AbstractPowerUp{
    public PowerupRobinHood(){}
    public PowerupRobinHood(Long ownerID, String ownerName){
        super(ownerID, ownerName, PowerupType.RobinHood.getDescription(), PowerupType.RobinHood.getName(), PowerupType.RobinHood);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        List<Player> players = new ArrayList<>();
        players.addAll(game.getPlayers()); //create deep copy

        players.sort(Comparator.comparingDouble(Player::getBalance).reversed().thenComparing(Player::getPlayerID));

        if(players.size() < 2)
            return instructions;

        Long topPlayerID = players.get(0).getPlayerID();

        if(this.ownerID.equals(topPlayerID))
            return instructions;

        double toDistribute = round(players.get(0).getBalance()*0.2);
        double share = round(toDistribute/(players.size()-1));

        for(Player player: players){
            Long playerID = player.getPlayerID();

            if(playerID.equals(topPlayerID))
                instructions.add(new Instruction(playerID, InstructionType.a5, toDistribute));
            else
                instructions.add(new Instruction(playerID, InstructionType.a7, share));
        }
        return instructions;
    }
}

