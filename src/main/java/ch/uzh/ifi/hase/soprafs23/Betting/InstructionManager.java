package ch.uzh.ifi.hase.soprafs23.Betting;

import ch.uzh.ifi.hase.soprafs23.constant.Direction;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.*;


@Entity(name = "PlayerManager")
@Table(name = "player_manager")
public class InstructionManager {

    @Id
    @GeneratedValue
    private Long instructionManagerID;

    @ElementCollection
    private List<Instruction> instructions;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_playerID")
    private Player player;


    public InstructionManager(){}

    public void init(Player player) {
        this.player = player;
        this.resetInstructions();

    }

    public void addInstruction(Instruction instruction){
        if(this.player.getPlayerID().equals(instruction.getOwnerID()))
            this.instructions.add(instruction);
    }

    public int computeNewBalance(Direction direction, double ratio){

        double outcome = 0.0;
        Direction betDirection = this.player.getCurrentBet().getDirection();
        double betAmount = this.player.getCurrentBet().getAmount();

        if(betDirection.equals(direction))
            outcome = 1.0;
        else if(!betDirection.equals(Direction.NONE))
            outcome = -1.0;

        HashMap<InstructionType, Double> temp = this.getMap();

        for(Instruction instruction: this.instructions){
            InstructionType type = instruction.getType();
            temp.put(type, instruction.compute(temp.get(type)));
        }

        this.resetInstructions();

        double betPart = (((ratio-1.0)*100+1)*betAmount*temp.get(InstructionType.A2) + temp.get(InstructionType.A3))*max(0,(1-temp.get(InstructionType.A20)));
        double balance = this.player.getBalance();

        double newBalance = temp.get(InstructionType.A18) + max(0,(1-temp.get(InstructionType.A19))) *
                            (temp.get(InstructionType.A0)
                            + temp.get(InstructionType.A1)*balance
                            + outcome*betPart
                            - min(min(1, temp.get(InstructionType.A4))*betPart*outcome, 0)
                            - temp.get(InstructionType.A5)*temp.get(InstructionType.A6)
                            + temp.get(InstructionType.A7)*temp.get(InstructionType.A8)
                            - temp.get(InstructionType.A9)*temp.get(InstructionType.A10)
                            + temp.get(InstructionType.A11)*temp.get(InstructionType.A12)
                            + min( -min(1,  temp.get(InstructionType.A13))*max(balance,0), 0)
                            + temp.get(InstructionType.A14)/(1+(temp.get(InstructionType.A15)-1)*abs(signum(temp.get(InstructionType.A15))))
                            + max(0, outcome)*min(1, temp.get(InstructionType.A16))*betPart
                            - max(0, -outcome)*min(1, temp.get(InstructionType.A17))*betPart);

        newBalance = round(newBalance);

        return (int) newBalance;
    }

    private HashMap<InstructionType, Double> getMap(){
        HashMap<InstructionType, Double> temp = new HashMap<>();

        for(InstructionType type: InstructionType.values()){
            temp.put(type, type.getDefaultValue());
        }
        return temp;
    }

    private void resetInstructions(){
        this.instructions = new ArrayList<>();
    }

}
