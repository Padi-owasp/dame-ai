package ai.dame.business.game;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GameStateTest {

    @Test
    void holdsBoardSideToMoveAndStatus() {
        GameState state = new GameState(Board.initial(), PlayerColor.WHITE, new GameStatus.InProgress());
        assertThat(state.sideToMove()).isEqualTo(PlayerColor.WHITE);
        assertThat(state.status()).isInstanceOf(GameStatus.InProgress.class);
    }

    @Test
    void winStatusCarriesTheWinner() {
        GameStatus.Win win = new GameStatus.Win(PlayerColor.WHITE);
        assertThat(win.winner()).isEqualTo(PlayerColor.WHITE);
    }

    @Test
    void rejectedResultCarriesReason() {
        MoveResult result = new MoveResult.Rejected(RejectionReason.GAME_OVER);
        assertThat(result).isInstanceOf(MoveResult.Rejected.class);
        assertThat(((MoveResult.Rejected) result).reason()).isEqualTo(RejectionReason.GAME_OVER);
    }
}
