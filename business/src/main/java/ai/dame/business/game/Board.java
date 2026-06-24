package ai.dame.business.game;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * An immutable snapshot of the board: a map from occupied {@link Square}s to the {@link Piece}
 * standing on them. Empty squares are simply absent from the map. {@link #place} and
 * {@link #remove} return new instances; the wrapped map is unmodifiable.
 */
public record Board(Map<Square, Piece> pieces) {

    public Board {
        pieces = Collections.unmodifiableMap(new HashMap<>(pieces));
    }

    /**
     * The standard Deutsche Dame opening position: 12 men per colour on the dark squares of
     * their first three rows — BLACK on rows 0–2, WHITE on rows 5–7.
     *
     * @return the initial board.
     */
    public static Board initial() {
        Map<Square, Piece> pieces = new HashMap<>();
        addMen(pieces, 0, 2, PlayerColor.BLACK);
        addMen(pieces, 5, 7, PlayerColor.WHITE);
        return new Board(pieces);
    }

    private static void addMen(Map<Square, Piece> pieces, int firstRow, int lastRow, PlayerColor color) {
        for (int row = firstRow; row <= lastRow; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 1) {
                    pieces.put(new Square(row, col), new Piece(color, PieceType.MAN));
                }
            }
        }
    }

    /** @return the piece on {@code square}, or empty if the square is unoccupied. */
    public Optional<Piece> at(Square square) {
        return Optional.ofNullable(pieces.get(square));
    }

    /** @return {@code true} if no piece stands on {@code square}. */
    public boolean isEmpty(Square square) {
        return !pieces.containsKey(square);
    }

    /** @return a new board with {@code piece} placed on {@code square}. */
    public Board place(Square square, Piece piece) {
        Map<Square, Piece> copy = new HashMap<>(pieces);
        copy.put(square, piece);
        return new Board(copy);
    }

    /** @return a new board with any piece on {@code square} removed. */
    public Board remove(Square square) {
        Map<Square, Piece> copy = new HashMap<>(pieces);
        copy.remove(square);
        return new Board(copy);
    }
}
