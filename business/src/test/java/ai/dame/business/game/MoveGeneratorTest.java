package ai.dame.business.game;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MoveGeneratorTest {

    private final MoveGenerator generator = new MoveGenerator();

    private GameState whiteToMove(Map<Square, Piece> pieces) {
        return new GameState(new Board(pieces), PlayerColor.WHITE, new GameStatus.InProgress());
    }

    private GameState blackToMove(Map<Square, Piece> pieces) {
        return new GameState(new Board(pieces), PlayerColor.BLACK, new GameStatus.InProgress());
    }

    @Nested
    class SimpleManMoves {

        @Test
        void openingPositionHasSevenSimpleWhiteMoves() {
            GameState state = new GameState(Board.initial(), PlayerColor.WHITE, new GameStatus.InProgress());
            List<Move> moves = generator.legalMoves(state);
            assertThat(moves).hasSize(7);
            assertThat(moves).allMatch(m -> m.steps().size() == 1);
        }

        @Test
        void whiteManMovesForwardOnBothDiagonals() {
            GameState state = whiteToMove(Map.of(new Square(4, 3), new Piece(PlayerColor.WHITE, PieceType.MAN)));
            assertThat(generator.legalMoves(state)).containsExactlyInAnyOrder(
                    Move.of(new Square(4, 3), new Square(3, 2)),
                    Move.of(new Square(4, 3), new Square(3, 4)));
        }

        @Test
        void blackManMovesTowardRowSeven() {
            GameState state = blackToMove(Map.of(new Square(3, 2), new Piece(PlayerColor.BLACK, PieceType.MAN)));
            assertThat(generator.legalMoves(state)).containsExactlyInAnyOrder(
                    Move.of(new Square(3, 2), new Square(4, 1)),
                    Move.of(new Square(3, 2), new Square(4, 3)));
        }

        @Test
        void occupiedDiagonalBlocksTheSimpleMove() {
            GameState state = whiteToMove(Map.of(
                    new Square(4, 3), new Piece(PlayerColor.WHITE, PieceType.MAN),
                    new Square(3, 2), new Piece(PlayerColor.WHITE, PieceType.MAN)));
            assertThat(generator.legalMoves(state)).containsExactlyInAnyOrder(
                    Move.of(new Square(4, 3), new Square(3, 4)),
                    Move.of(new Square(3, 2), new Square(2, 1)),
                    Move.of(new Square(3, 2), new Square(2, 3)));
        }

        @Test
        void edgeManHasOnlyOneDiagonal() {
            GameState state = whiteToMove(Map.of(new Square(5, 0), new Piece(PlayerColor.WHITE, PieceType.MAN)));
            assertThat(generator.legalMoves(state)).containsExactly(
                    Move.of(new Square(5, 0), new Square(4, 1)));
        }
    }

    @Nested
    class ManCaptures {

        @Test
        void captureIsForcedAndSuppressesSimpleMoves() {
            GameState state = whiteToMove(Map.of(
                    new Square(5, 2), new Piece(PlayerColor.WHITE, PieceType.MAN),
                    new Square(4, 3), new Piece(PlayerColor.BLACK, PieceType.MAN)));
            assertThat(generator.legalMoves(state)).containsExactly(
                    Move.of(new Square(5, 2), new Square(3, 4)));
        }

        @Test
        void menCannotCaptureBackward() {
            // Enemy sits behind the white man (toward row 7); no backward capture is generated.
            GameState state = whiteToMove(Map.of(
                    new Square(3, 4), new Piece(PlayerColor.WHITE, PieceType.MAN),
                    new Square(4, 3), new Piece(PlayerColor.BLACK, PieceType.MAN)));
            assertThat(generator.legalMoves(state)).containsExactlyInAnyOrder(
                    Move.of(new Square(3, 4), new Square(2, 3)),
                    Move.of(new Square(3, 4), new Square(2, 5)));
        }

        @Test
        void multiCaptureIsASingleMoveWithOrderedSteps() {
            GameState state = whiteToMove(Map.of(
                    new Square(5, 2), new Piece(PlayerColor.WHITE, PieceType.MAN),
                    new Square(4, 3), new Piece(PlayerColor.BLACK, PieceType.MAN),
                    new Square(2, 5), new Piece(PlayerColor.BLACK, PieceType.MAN)));
            List<Move> moves = generator.legalMoves(state);
            assertThat(moves).containsExactly(
                    Move.of(new Square(5, 2), new Square(3, 4), new Square(1, 6)));
            assertThat(moves).doesNotContain(
                    Move.of(new Square(5, 2), new Square(3, 4))); // partial chain is illegal
        }

        @Test
        void branchingChainsProduceOneMovePerCompletePath() {
            GameState state = whiteToMove(Map.of(
                    new Square(5, 4), new Piece(PlayerColor.WHITE, PieceType.MAN),
                    new Square(4, 3), new Piece(PlayerColor.BLACK, PieceType.MAN),
                    new Square(2, 1), new Piece(PlayerColor.BLACK, PieceType.MAN),
                    new Square(2, 3), new Piece(PlayerColor.BLACK, PieceType.MAN)));
            assertThat(generator.legalMoves(state)).containsExactlyInAnyOrder(
                    Move.of(new Square(5, 4), new Square(3, 2), new Square(1, 0)),
                    Move.of(new Square(5, 4), new Square(3, 2), new Square(1, 4)));
        }
    }
}
