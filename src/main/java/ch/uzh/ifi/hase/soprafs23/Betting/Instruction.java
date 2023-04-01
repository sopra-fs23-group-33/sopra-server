package ch.uzh.ifi.hase.soprafs23.Betting;

import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;

@Embeddable
public class Instruction {

    @Column(name = "ownerID")
    private long ownerID;
    @Enumerated
    private InstructionType type;
    @Column(name = "number")
    private double number;

    public Instruction(Long ownerID, InstructionType type, int number){
        this.number = number;
        this.ownerID = ownerID;
        this.type = type;
    }

    public Instruction(){}

    public InstructionType getType() {
        return type;
    }

    public double getNumber() {
        return number;
    }

    public long getOwnerID() {
        return ownerID;
    }

    public Double compute(Double a){
        return this.type.compute(a, this.number);
    }
}
