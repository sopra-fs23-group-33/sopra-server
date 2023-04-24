package ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
public class PowerupIncendiary extends AbstractPowerUp{
    private static Random random = new Random();
    public PowerupIncendiary(){}
    public PowerupIncendiary(Long ownerID, String ownerName){
        super(ownerID, ownerName, PowerupType.INCENDIARY.getDescription(), PowerupType.INCENDIARY.getName(), PowerupType.INCENDIARY);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        List<Player> players = new ArrayList<>();
        players.addAll(game.getPlayers()); //create deep copy

        if(players.size() < 2)
            return instructions;

        int index = random.nextInt(players.size());

        if(index > players.size())
            index = 0;

        Player player = players.get(index);

        instructions.add(new Instruction(player.getPlayerID(), InstructionType.A13, 1));

        return instructions;
    }
}
