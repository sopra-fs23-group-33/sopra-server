package ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.exceptions.PlayerNotFoundException;

import javax.persistence.Entity;
import java.util.ArrayList;

@Entity
public class PowerupLifeLine extends AbstractPowerUp{

    public PowerupLifeLine(){}
    public PowerupLifeLine(Long ownerID, String ownerName){
        super(ownerID, ownerName,  PowerupType.LifeLine.getDescription(),  PowerupType.LifeLine.getName(), PowerupType.LifeLine);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        try{
            Player player = game.findPlayerByID(ownerID);

            if(player.getBalance() <= 0){
                int difference = 500 - player.getBalance();

                Instruction instruction1 = new Instruction(this.ownerID, InstructionType.a14, difference);
                Instruction instruction2 = new Instruction(this.ownerID, InstructionType.a15, 1);
                instructions.add(instruction1);
                instructions.add(instruction2);
            }
        }
        catch (PlayerNotFoundException ignored){
        }

        return instructions;
    }
}

