package ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;

import javax.persistence.Entity;
import java.util.ArrayList;

@Entity
public class PowerupHighRiskHighReward100 extends AbstractPowerUp{
    public PowerupHighRiskHighReward100(){}
    public PowerupHighRiskHighReward100(Long ownerID, String ownerName){
        super(ownerID, ownerName, PowerupType.RiskReward100.getDescription(), PowerupType.RiskReward100.getName(), PowerupType.RiskReward100);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        Instruction instruction = new Instruction(this.ownerID, InstructionType.a3, 100);
        instructions.add(instruction);
        return instructions;
    }
}
