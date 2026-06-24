package ai.dame.business.game;

/** An immutable snapshot of a game: the board, whose turn it is, and the current status. */
public record GameState(Board board, PlayerColor sideToMove, GameStatus status) {
}
