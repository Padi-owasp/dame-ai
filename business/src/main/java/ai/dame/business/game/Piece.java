package ai.dame.business.game;

/** A single piece: its owning colour and whether it is a man or a Dame. */
public record Piece(PlayerColor color, PieceType type) {

    /** @return {@code true} if this piece is an ordinary man. */
    public boolean isMan() {
        return type == PieceType.MAN;
    }

    /** @return {@code true} if this piece is a Dame (flying king). */
    public boolean isDame() {
        return type == PieceType.DAME;
    }
}
