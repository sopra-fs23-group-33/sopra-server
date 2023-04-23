package ch.uzh.ifi.hase.soprafs23.Powerups;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;

import javax.persistence.Entity;
import java.util.ArrayList;
@Entity
public class PowerupPlus200 extends AbstractPowerUp{

    public PowerupPlus200(){}
    public PowerupPlus200(Long ownerID){
        super(ownerID, "this powerup adds 200 coins to your balance", PowerupType.Plus200);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        Instruction instruction = new Instruction(this.ownerID, InstructionType.a0, 200);
        instructions.add(instruction);
        return instructions;
    }
}