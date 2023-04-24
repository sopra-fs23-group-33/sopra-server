package ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;

import javax.persistence.Entity;
import java.util.ArrayList;

@Entity
public class PowerupHighRiskHighReward200 extends AbstractPowerUp{
    public PowerupHighRiskHighReward200(){}
    public PowerupHighRiskHighReward200(Long ownerID, String ownerName){
        super(ownerID, ownerName, PowerupType.RISK_REWARD200.getDescription(), PowerupType.RISK_REWARD200.getName(), PowerupType.RISK_REWARD200);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        Instruction instruction = new Instruction(this.ownerID, InstructionType.A3, 200);
        instructions.add(instruction);
        return instructions;
    }
}

