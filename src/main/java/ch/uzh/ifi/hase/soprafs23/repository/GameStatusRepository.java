package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.Game.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("gameStatusRepository")
public interface GameStatusRepository extends JpaRepository<GameStatus, Long> {
    GameStatus findByGameStatusID(Long gameStatusID);
}

