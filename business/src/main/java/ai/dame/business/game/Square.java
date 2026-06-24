package ai.dame.business.game;

/**
 * A playable position on the board, addressed by {@code row} and {@code col} (both 0–7,
 * with row 0 at the top). Only dark squares — those where {@code (row + col)} is odd — are
 * playable; the compact canonical constructor rejects anything else so an invalid square
 * cannot be constructed.
 */
public record Square(int row, int col) {

    /**
     * Canonical constructor for Square.
     *
     * @param row the row coordinate (0–7)
     * @param col the column coordinate (0–7)
     * @throws IllegalArgumentException if coordinates are off the board (outside 0–7 range)
     *                                  or if the square is not dark (i.e., {@code (row + col)}
     *                                  is even)
     */
    public Square {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            throw new IllegalArgumentException("Square off the board: (" + row + ", " + col + ")");
        }
        if ((row + col) % 2 == 0) {
            throw new IllegalArgumentException("Not a playable (dark) square: (" + row + ", " + col + ")");
        }
    }
}
