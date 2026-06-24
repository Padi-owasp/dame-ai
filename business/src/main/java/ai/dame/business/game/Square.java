package ai.dame.business.game;

/**
 * A playable position on the board, addressed by {@code row} and {@code col} (both 0–7,
 * with row 0 at the top). Only dark squares — those where {@code (row + col)} is odd — are
 * playable; the compact canonical constructor rejects anything else so an invalid square
 * cannot be constructed.
 */
public record Square(int row, int col) {

    public Square {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            throw new IllegalArgumentException("Square off the board: (" + row + ", " + col + ")");
        }
        if ((row + col) % 2 == 0) {
            throw new IllegalArgumentException("Not a playable (dark) square: (" + row + ", " + col + ")");
        }
    }
}
