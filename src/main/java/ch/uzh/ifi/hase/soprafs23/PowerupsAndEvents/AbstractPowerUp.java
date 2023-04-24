package ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractPowerUp {
    @Id
    @GeneratedValue
    Long powerupID;

    @Column(name = "ownerID")
    Long ownerID;

    @Column(name = "ownerName")
    String ownerName;

    @Column(name = "Description")
    String description;

    @Column(name = "name")
    String name;

    @Column(name = "active")
    boolean active;

    @Column(name =  "powerupType")
    @Enumerated(EnumType.STRING)
    PowerupType powerupType;

    public AbstractPowerUp(){}

    public AbstractPowerUp(Long ownerID, String ownerName, String description, String name, PowerupType type){
        this.ownerID = ownerID;
        this.description = description;
        this.active = false;
        this.powerupType = type;
        this.name = name;
        this.ownerName = ownerName;
    }

    public void activate(){
        this.active = true;
    }

    public abstract ArrayList<Instruction> generateInstructions(Game game);

    public Long getPowerupID() {
        return powerupID;
    }

    public Long getOwnerID() {
        return ownerID;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public PowerupType getPowerupType() {
        return powerupType;
    }

    public void setPowerupID(Long powerupID) {
        this.powerupID = powerupID;
    }

    public String getName() {
        return name;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public boolean equals(Object other){
        if(other == null) {
            return false;
        } else if(other == this ) {
            return true;
        } else if(other.getClass() != getClass()) {
            return false;
        } else {
            return Objects.equals(((AbstractPowerUp) other).powerupID, this.powerupID);
        }
    }

    @Override
    public int hashCode(){
        return this.powerupID.hashCode();
    }

}
