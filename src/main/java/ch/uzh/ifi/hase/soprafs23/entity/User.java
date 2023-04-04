package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.UserState;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long userID;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @CreatedDate
    private LocalDate creationDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserState status;

    @Column(nullable = false)
    private int totalRoundsPlayed;

    @Column(nullable = false)
    private int numberOfBetsWon;

    @Column(nullable = false)
    private int numberOfBetsLost;

    @Column(nullable = false, unique = true)
    private String token;

    @Column
    private int rank;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = UserState.ONLINE;
        this.creationDate = LocalDate.now();
        this.totalRoundsPlayed = 0;
        this.numberOfBetsWon = 0;
        this.numberOfBetsLost = 0;
        this.token = UUID.randomUUID().toString();
        this.rank = -1;
    }


    public Long getUserID() {
        return this.userID;
    }

    public void setUserID(Long id) {
        this.userID = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public UserState getState() {
        return status;
    }

    public void setState(UserState status) {
        this.status = status;
    }

    public int getTotalRoundsPlayed() {
        return totalRoundsPlayed;
    }

    public void setTotalRoundsPlayed(int totalRoundsPlayed) {
        this.totalRoundsPlayed = totalRoundsPlayed;
    }

    public int getNumberOfBetsWon() {
        return numberOfBetsWon;
    }

    public void setNumberOfBetsWon(int numberOfBetsWon) {
        this.numberOfBetsWon = numberOfBetsWon;
    }

    public int getNumberOfBetsLost() {
        return numberOfBetsLost;
    }

    public void setNumberOfBetsLost(int numberOfBetsLost) {
        this.numberOfBetsLost = numberOfBetsLost;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }


    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        else if (other == this) {
            return true;
        }
        else if (other.getClass() != getClass()) {
            return false;
        }
        else {
            return Objects.equals(((User) other).username, this.username);
        }
    }


    @Override
    public int hashCode() {
        return this.username.hashCode();
    }


    public void incrementTotalRoundsPlayed() {
        this.totalRoundsPlayed = this.totalRoundsPlayed + 1;
    }

    public void incrementNumberOfBetsWon() {
        this.numberOfBetsWon = this.numberOfBetsWon + 1;
    }

    public void incrementNumberOfBetsLost() {
        this.numberOfBetsLost = this.numberOfBetsLost + 1;
    }
}
