package ai.dame.business.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumerates the legal moves for a {@link GameState}. Schlagzwang is applied here: if any
 * capture is available for the side to move, only captures are returned. This class is the
 * sole authority that {@link RulesEngine} consults, so legality and move generation never
 * drift apart.
 */
public final class MoveGenerator {

    /**
     * Returns exactly the legal moves for the side to move. If at least one capture exists,
     * only captures are returned (Schlagzwang); otherwise the simple moves are returned.
     *
     * @param state the position to analyse (its status is not consulted here).
     * @return the legal moves, possibly empty (which means the side to move has lost).
     */
    public List<Move> legalMoves(GameState state) {
        List<Move> captures = new ArrayList<>();
        List<Move> simple = new ArrayList<>();
        Board board = state.board();
        for (Square from : board.pieces().keySet()) {
            Piece piece = board.at(from).orElseThrow();
            if (piece.color() != state.sideToMove()) {
                continue;
            }
            List<Move> pieceCaptures = capturesFrom(board, from, piece);
            if (pieceCaptures.isEmpty()) {
                simple.addAll(simpleMovesFrom(board, from, piece));
            } else {
                captures.addAll(pieceCaptures);
            }
        }
        return captures.isEmpty() ? simple : captures;
    }

    private List<Move> simpleMovesFrom(Board board, Square from, Piece piece) {
        return piece.isDame() ? dameSimpleMoves(board, from, piece) : manSimpleMoves(board, from, piece);
    }

    private List<Move> manSimpleMoves(Board board, Square from, Piece piece) {
        List<Move> moves = new ArrayList<>();
        int forward = forwardDir(piece.color());
        for (int dc : new int[] {-1, 1}) {
            int row = from.row() + forward;
            int col = from.col() + dc;
            if (isOnBoard(row, col) && board.isEmpty(new Square(row, col))) {
                moves.add(Move.of(from, new Square(row, col)));
            }
        }
        return moves;
    }

    /** Dame simple moves are implemented in a later task. */
    private List<Move> dameSimpleMoves(Board board, Square from, Piece piece) {
        return List.of();
    }

    /** Capture generation is implemented in a later task. */
    private List<Move> capturesFrom(Board board, Square from, Piece piece) {
        return List.of();
    }

    private static int forwardDir(PlayerColor color) {
        return color == PlayerColor.WHITE ? -1 : 1; // WHITE advances toward row 0
    }

    private static boolean isOnBoard(int row, int col) {
        return row >= 0 && row <= 7 && col >= 0 && col <= 7;
    }
}
