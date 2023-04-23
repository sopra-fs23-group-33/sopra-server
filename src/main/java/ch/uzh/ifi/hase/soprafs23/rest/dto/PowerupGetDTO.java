package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.Powerups.PowerupType;

public class PowerupGetDTO {
    private Long powerupID;
    private PowerupType powerupType;
    private String description;
    private Long ownerID;
    private boolean active;

    public Long getPowerupID() {
        return powerupID;
    }

    public void setPowerupID(Long powerupID) {
        this.powerupID = powerupID;
    }

    public PowerupType getPowerupType() {
        return powerupType;
    }

    public void setPowerupType(PowerupType powerupType) {
        this.powerupType = powerupType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Long ownerID) {
        this.ownerID = ownerID;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
