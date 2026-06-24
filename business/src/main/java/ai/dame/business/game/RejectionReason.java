package ai.dame.business.game;

/** Why the engine rejected a submitted move. */
public enum RejectionReason {
    /** The game is already over. */
    GAME_OVER,
    /** There is no piece on the move's origin square. */
    NO_PIECE_AT_FROM,
    /** The piece on the origin square does not belong to the side to move. */
    NOT_YOUR_PIECE,
    /** A simple move was submitted while at least one capture is available (Schlagzwang). */
    CAPTURE_REQUIRED,
    /** The move is a valid prefix of a longer mandatory capture chain that must be completed. */
    INCOMPLETE_CAPTURE,
    /** The move does not correspond to any legal move for any other reason. */
    ILLEGAL_PATH
}
