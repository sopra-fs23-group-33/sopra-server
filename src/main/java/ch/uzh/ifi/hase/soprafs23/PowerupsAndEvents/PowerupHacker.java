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
public class PowerupHacker extends AbstractPowerUp{
    public PowerupHacker(){}
    public PowerupHacker(Long ownerID, String ownerName){
        super(ownerID,ownerName,  PowerupType.Hacker.getDescription(),  PowerupType.Hacker.getName() , PowerupType.Hacker);
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

        instructions.add(new Instruction(topPlayerID, InstructionType.a9, 100));
        instructions.add(new Instruction(this.ownerID, InstructionType.a11, 100));

        return instructions;
    }
}
