package ai.dame.business.game;

/** The outcome of applying a move: either the resulting state or a rejection. */
public sealed interface MoveResult permits MoveResult.Applied, MoveResult.Rejected {

    /** The move was legal and produced {@code newState}. */
    record Applied(GameState newState) implements MoveResult {
    }

    /** The move was illegal; {@code reason} explains why. */
    record Rejected(RejectionReason reason) implements MoveResult {
    }
}
