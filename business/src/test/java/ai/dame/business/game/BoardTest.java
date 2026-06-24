package ai.dame.business.game;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class BoardTest {

    @Test
    void initialBoardHasTwelveMenPerColourOnDarkSquares() {
        Board board = Board.initial();

        long white = board.pieces().values().stream()
                .filter(p -> p.color() == PlayerColor.WHITE).count();
        long black = board.pieces().values().stream()
                .filter(p -> p.color() == PlayerColor.BLACK).count();

        assertThat(white).isEqualTo(12);
        assertThat(black).isEqualTo(12);
        assertThat(board.pieces().values()).allMatch(Piece::isMan);
    }

    @Test
    void blackStartsOnTopRowsWhiteOnBottomRows() {
        Board board = Board.initial();

        assertThat(board.at(new Square(0, 1)))
                .contains(new Piece(PlayerColor.BLACK, PieceType.MAN));
        assertThat(board.at(new Square(7, 0)))
                .contains(new Piece(PlayerColor.WHITE, PieceType.MAN));
        // middle rows 3 and 4 are empty
        assertThat(board.isEmpty(new Square(3, 0))).isTrue();
        assertThat(board.isEmpty(new Square(4, 1))).isTrue();
    }

    @Test
    void placeAndRemoveReturnNewBoardsWithoutMutatingOriginal() {
        Board board = Board.initial();
        Square target = new Square(4, 1);

        Board placed = board.place(target, new Piece(PlayerColor.WHITE, PieceType.DAME));
        assertThat(board.isEmpty(target)).isTrue();              // original unchanged
        assertThat(placed.at(target))
                .contains(new Piece(PlayerColor.WHITE, PieceType.DAME));

        Board removed = placed.remove(target);
        assertThat(removed.isEmpty(target)).isTrue();
        assertThat(placed.at(target)).isPresent();               // intermediate unchanged
    }

    @Test
    void piecesMapIsUnmodifiable() {
        Board board = Board.initial();
        org.assertj.core.api.Assertions.assertThatThrownBy(
                () -> board.pieces().put(new Square(4, 1), new Piece(PlayerColor.WHITE, PieceType.MAN)))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
