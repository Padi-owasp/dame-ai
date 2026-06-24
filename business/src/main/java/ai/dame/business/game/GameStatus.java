package ai.dame.business.game;

/** The status of a game: still running, or won by a player. (Draws are decided outside the engine.) */
public sealed interface GameStatus permits GameStatus.InProgress, GameStatus.Win {

    /** The game is ongoing. */
    record InProgress() implements GameStatus {
    }

    /** The game has been won by {@code winner} (the opponent has no legal move). */
    record Win(PlayerColor winner) implements GameStatus {
    }
}
