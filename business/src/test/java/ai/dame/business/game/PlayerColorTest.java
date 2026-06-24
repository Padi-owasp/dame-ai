package ai.dame.business.game;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PlayerColorTest {

    @Test
    void opponentOfWhiteIsBlack() {
        assertThat(PlayerColor.WHITE.opponent()).isEqualTo(PlayerColor.BLACK);
    }

    @Test
    void opponentOfBlackIsWhite() {
        assertThat(PlayerColor.BLACK.opponent()).isEqualTo(PlayerColor.WHITE);
    }
}
