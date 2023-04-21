package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("gameRoundRepository")
public interface GameRoundRepository extends JpaRepository<GameRound, Long> {
    GameRound findByRoundID(Long roundID);

    List<GameRound> findTop8ByUsageOrderByRoundIDDesc(boolean usage);

    List<GameRound> findTop8ByUsageOrderByRoundIDAsc(boolean usage);



    long countByUsageFalse();

}
