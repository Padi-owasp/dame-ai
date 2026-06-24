package ai.dame.business.game;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PieceTest {

    @Test
    void manReportsItsType() {
        Piece man = new Piece(PlayerColor.WHITE, PieceType.MAN);
        assertThat(man.isMan()).isTrue();
        assertThat(man.isDame()).isFalse();
    }

    @Test
    void dameReportsItsType() {
        Piece dame = new Piece(PlayerColor.BLACK, PieceType.DAME);
        assertThat(dame.isDame()).isTrue();
        assertThat(dame.isMan()).isFalse();
    }
}
