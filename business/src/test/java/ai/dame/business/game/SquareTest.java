package ai.dame.business.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class SquareTest {

    @Test
    void acceptsDarkSquares() {
        Square square = new Square(0, 1);
        assertThat(square.row()).isEqualTo(0);
        assertThat(square.col()).isEqualTo(1);

        Square other = new Square(1, 0);
        assertThat(other.row()).isEqualTo(1);
        assertThat(other.col()).isEqualTo(0);
    }

    @Test
    void rejectsLightSquares() {
        assertThatThrownBy(() -> new Square(0, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsOffBoardCoordinates() {
        assertThatThrownBy(() -> new Square(-1, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Square(8, 1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Square(0, -1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Square(1, 8))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
