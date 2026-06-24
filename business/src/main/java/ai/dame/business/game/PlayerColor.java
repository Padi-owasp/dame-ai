package ai.dame.business.game;

/** The two players, identified by piece colour. WHITE moves first. */
public enum PlayerColor {
    WHITE,
    BLACK;

    /** Returns the other colour. */
    public PlayerColor opponent() {
        return this == WHITE ? BLACK : WHITE;
    }
}
