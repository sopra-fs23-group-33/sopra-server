package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("gameRoundRepository")
public interface GameRoundRepository extends JpaRepository<GameRound, Long> {
  GameRound findByRoundID(Long roundID);

}
