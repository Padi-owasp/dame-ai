package ai.dame.business.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class MoveTest {

    @Test
    void destinationIsTheLastStep() {
        Move move = Move.of(new Square(5, 2), new Square(4, 3), new Square(2, 5));
        assertThat(move.destination()).isEqualTo(new Square(2, 5));
    }

    @Test
    void singleStepMoveHasThatStepAsDestination() {
        Move move = Move.of(new Square(5, 2), new Square(4, 3));
        assertThat(move.destination()).isEqualTo(new Square(4, 3));
    }

    @Test
    void rejectsEmptySteps() {
        assertThatThrownBy(() -> new Move(new Square(5, 2), List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equalMovesAreValueEqual() {
        assertThat(Move.of(new Square(5, 2), new Square(4, 3)))
                .isEqualTo(Move.of(new Square(5, 2), new Square(4, 3)));
    }
}
