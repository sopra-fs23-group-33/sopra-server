package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents.AbstractPowerUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("powerupRepository")
public interface PowerupRepository extends JpaRepository<AbstractPowerUp, Long> {

    AbstractPowerUp findByPowerupID(Long powerupID);
}
