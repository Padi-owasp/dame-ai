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

    /**
     * All maximal capture sequences for the piece on {@code from}. The moving piece is lifted
     * off the board for the search; each captured piece is removed as the search advances, so a
     * piece is never jumped twice and already-vacated squares are passable.
     */
    private List<Move> capturesFrom(Board board, Square from, Piece piece) {
        Board lifted = board.remove(from);
        List<List<Square>> paths = new ArrayList<>();
        searchCaptures(lifted, from, piece, new ArrayList<>(), paths);
        List<Move> moves = new ArrayList<>();
        for (List<Square> steps : paths) {
            moves.add(new Move(from, steps));
        }
        return moves;
    }

    private void searchCaptures(Board board, Square current, Piece piece,
            List<Square> steps, List<List<Square>> paths) {
        List<int[]> immediate = immediateCaptures(board, current, piece);
        if (immediate.isEmpty()) {
            if (!steps.isEmpty()) {
                paths.add(new ArrayList<>(steps)); // maximal chain reached
            }
            return;
        }
        for (int[] capture : immediate) {
            Square captured = new Square(capture[0], capture[1]);
            Square landing = new Square(capture[2], capture[3]);
            steps.add(landing);
            // The piece keeps its current type for the rest of the chain: a man passing through
            // the back row mid-chain is NOT promoted and gains no Dame powers (see RulesEngine).
            searchCaptures(board.remove(captured), landing, piece, steps, paths);
            steps.remove(steps.size() - 1);
        }
    }

    private List<int[]> immediateCaptures(Board board, Square from, Piece piece) {
        return piece.isDame() ? dameCaptures(board, from, piece) : manCaptures(board, from, piece);
    }

    private List<int[]> manCaptures(Board board, Square from, Piece piece) {
        List<int[]> captures = new ArrayList<>();
        int forward = forwardDir(piece.color());
        for (int dc : new int[] {-1, 1}) {
            int midRow = from.row() + forward;
            int midCol = from.col() + dc;
            int landRow = from.row() + 2 * forward;
            int landCol = from.col() + 2 * dc;
            if (!isOnBoard(landRow, landCol)) {
                continue;
            }
            Square mid = new Square(midRow, midCol);
            Square landing = new Square(landRow, landCol);
            if (isEnemy(board, mid, piece) && board.isEmpty(landing)) {
                captures.add(new int[] {midRow, midCol, landRow, landCol});
            }
        }
        return captures;
    }

    /** Dame captures are implemented in a later task. */
    private List<int[]> dameCaptures(Board board, Square from, Piece piece) {
        return List.of();
    }

    private boolean isEnemy(Board board, Square square, Piece piece) {
        return board.at(square).map(other -> other.color() != piece.color()).orElse(false);
    }

    private static int forwardDir(PlayerColor color) {
        return color == PlayerColor.WHITE ? -1 : 1; // WHITE advances toward row 0
    }

    private static boolean isOnBoard(int row, int col) {
        return row >= 0 && row <= 7 && col >= 0 && col <= 7;
    }
}
