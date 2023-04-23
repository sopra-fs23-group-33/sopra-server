package ch.uzh.ifi.hase.soprafs23.Powerups;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;

import javax.persistence.Entity;
import java.util.ArrayList;
@Entity
public class PowerupPlus500 extends AbstractPowerUp{

    public PowerupPlus500(){}
    public PowerupPlus500(Long ownerID){
        super(ownerID, "this powerup adds 500 coins to your balance", PowerupType.Plus500);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        Instruction instruction = new Instruction(this.ownerID, InstructionType.a0, 500);
        instructions.add(instruction);
        return instructions;
    }
}