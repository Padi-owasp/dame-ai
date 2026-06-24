package ai.dame.business.game;

import java.util.List;

/**
 * A move intention for one turn: the origin square plus the ordered list of squares the
 * piece lands on. A simple move has a single step; a multi-capture has one step per jump.
 * The engine derives which pieces are captured from the geometry of the path.
 */
public record Move(Square from, List<Square> steps) {

    public Move {
        steps = List.copyOf(steps);
        if (steps.isEmpty()) {
            throw new IllegalArgumentException("A move must have at least one step");
        }
    }

    /** Convenience factory: {@code Move.of(from, step1, step2, ...)}. */
    public static Move of(Square from, Square... steps) {
        return new Move(from, List.of(steps));
    }

    /** @return the final landing square of the move. */
    public Square destination() {
        return steps.get(steps.size() - 1);
    }
}
