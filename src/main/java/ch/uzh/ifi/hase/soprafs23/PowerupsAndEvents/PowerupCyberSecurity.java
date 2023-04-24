package ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
public class PowerupCyberSecurity extends AbstractPowerUp{
    public PowerupCyberSecurity(){}
    public PowerupCyberSecurity(Long ownerID, String ownerName){
        super(ownerID, ownerName, PowerupType.CYBER_SECURITY.getDescription(), PowerupType.CYBER_SECURITY.getName(), PowerupType.CYBER_SECURITY);
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

        if(!this.ownerID.equals(topPlayerID))
            return instructions;

        for(Player player: players){
            Long playerID = player.getPlayerID();

            if(playerID.equals(topPlayerID))
                instructions.add(new Instruction(playerID, InstructionType.A10, 0));
            else
                instructions.add(new Instruction(playerID, InstructionType.A12, 0));
        }
        return instructions;
    }
}
