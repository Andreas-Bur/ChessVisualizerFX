package com.andbur;

import com.andbur.models.*;
import com.andbur.models.moves.CastleMove;
import com.andbur.models.moves.EnPassantMove;
import com.andbur.models.moves.Move;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoveController {
    private final TileModel[][] tileModels;
    private final int[][] knightOffsets = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {-1, 2}, {1, -2}, {-1, -2}};

    private final ListProperty<Move> moveHistory = new SimpleListProperty<>(FXCollections.observableArrayList());

    public MoveController(TileModel[][] tiles) {
        this.tileModels = tiles;
    }

    public Set<Move> getPotentialMoves(PieceModel pieceModel) {
        if (pieceModel.pieceType() == PieceType.PAWN) {
            return getLegalPawnMoves(pieceModel);
        } else if (pieceModel.pieceType() == PieceType.KING) {
            return getLegalKingMoves(pieceModel);
        } else {
            return getNormalMoves(pieceModel);
        }
    }

    private Set<Move> getLegalPawnMoves(PieceModel pieceModel) {
        Set<Move> legalMoves = new HashSet<>();
        int nextRank = pieceModel.getPosition().rank() + pieceModel.side().getDirection();
        int file = pieceModel.getPosition().file();
        if (hasNotMoved(pieceModel) && !tileModels[file][nextRank + pieceModel.side().getDirection()].isFull()) {
            Position destination = new Position(nextRank + pieceModel.side().getDirection(), file);
            legalMoves.add(new Move(tileModels, pieceModel, destination, null));
        }
        legalMoves.add(new Move(tileModels, pieceModel, new Position(nextRank, file), null));

        Set<Position> takePositions = getSeenFields(pieceModel);
        takePositions.retainAll(getOccupiedFields(pieceModel.side().other()));

        for (Position position : takePositions) {
            legalMoves.add(new Move(tileModels, pieceModel, position, tileModels[position.file()][position.rank()].getPieceModel()));
        }

        // en passant
        if (moveHistory.size() > 0) {
            Move lastMove = moveHistory.get(moveHistory.size() - 1);
            List<PieceModel> movedPieces = lastMove.getMovedPieces();
            if (movedPieces.size() == 1 && movedPieces.get(0).pieceType() == PieceType.PAWN) {
                if (Math.abs(lastMove.getDestinationPos().rank() - lastMove.getStartPos().rank()) == 2
                        && lastMove.getDestinationPos().file() == lastMove.getStartPos().file()) {
                    if (pieceModel.getPosition().rank() == movedPieces.get(0).getPosition().rank()) {
                        if (Math.abs(pieceModel.getPosition().file() - movedPieces.get(0).getPosition().file()) == 1) {
                            Position position = new Position(pieceModel.getPosition().rank() + pieceModel.side().getDirection(), movedPieces.get(0).getPosition().file());
                            legalMoves.add(new EnPassantMove(tileModels, pieceModel, position, movedPieces.get(0)));
                        }
                    }
                }
            }
        }

        return legalMoves;
    }

    private Set<Move> getLegalKingMoves(PieceModel pieceModel) {
        Set<Move> moves = new HashSet<>(getNormalMoves(pieceModel));
        if (hasNotMoved(pieceModel) && !isInCheck(pieceModel.side())) {
            // short castle
            if (!tileModels[5][pieceModel.getPosition().rank()].isFull() && !tileModels[6][pieceModel.getPosition().rank()].isFull()) {
                PieceModel rookModel = tileModels[7][pieceModel.getPosition().rank()].getPieceModel();
                if (tileModels[7][pieceModel.getPosition().rank()].isFull() && rookModel.pieceType() == PieceType.ROOK && hasNotMoved(rookModel)) {
                    Set<Position> seenFields = getAllSeenFields(pieceModel.side().other());
                    if (!seenFields.contains(new Position(pieceModel.getPosition().rank(), 5))
                            && !seenFields.contains(new Position(pieceModel.getPosition().rank(), 6))) {
                        moves.add(new CastleMove(tileModels, pieceModel, new Position(pieceModel.getPosition().rank(), 6)));
                    }
                }
            }
            //long castle
            if (!tileModels[3][pieceModel.getPosition().rank()].isFull()
                    && !tileModels[2][pieceModel.getPosition().rank()].isFull()
                    && !tileModels[1][pieceModel.getPosition().rank()].isFull()) {
                PieceModel rookModel = tileModels[0][pieceModel.getPosition().rank()].getPieceModel();
                if (tileModels[0][pieceModel.getPosition().rank()].isFull() && rookModel.pieceType() == PieceType.ROOK && hasNotMoved(rookModel)) {
                    Set<Position> seenFields = getAllSeenFields(pieceModel.side().other());
                    if (!seenFields.contains(new Position(pieceModel.getPosition().rank(), 2))
                            && !seenFields.contains(new Position(pieceModel.getPosition().rank(), 3))) {
                        moves.add(new CastleMove(tileModels, pieceModel, new Position(pieceModel.getPosition().rank(), 2)));
                    }
                }
            }
        }
        return moves;
    }

    public Set<Move> getNormalMoves(PieceModel pieceModel) {
        Set<Position> positions = getSeenFields(pieceModel);
        positions.removeAll(getOccupiedFields(pieceModel.side()));

        Set<Move> moves = new HashSet<>();
        for (Position position : positions) {
            moves.add(new Move(tileModels, pieceModel, position, tileModels[position.file()][position.rank()].getPieceModel()));
        }
        return moves;
    }

    public Set<Position> getOccupiedFields(Sides side) {
        Set<Position> occupiedFields = new HashSet<>();
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                if (tileModels[file][rank].isFull() && tileModels[file][rank].getPieceModel().side() == side) {
                    occupiedFields.add(tileModels[file][rank].getPosition());
                }
            }
        }
        return occupiedFields;
    }

    private Set<Position> getAllSeenFields(Sides side) {
        Set<Position> seenFields = new HashSet<>();
        for (Position position : getOccupiedFields(side)) {
            seenFields.addAll(getSeenFields(tileModels[position.file()][position.rank()].getPieceModel()));
        }
        return seenFields;
    }

    private Set<Position> getSeenFields(PieceModel pieceModel) {
        Set<Position> seenFields = new HashSet<>();
        int rank = pieceModel.getPosition().rank();
        int file = pieceModel.getPosition().file();
        switch (pieceModel.pieceType()) {
            case KING -> {
                for (int rankOff = -1; rankOff <= 1; rankOff++) {
                    for (int fileOff = -1; fileOff <= 1; fileOff++) {
                        if ((rank != 0 || file != 0) && isPositionOnBoard(rank + rankOff, file + fileOff)) {
                            seenFields.add(new Position(rank + rankOff, file + fileOff));
                        }
                    }
                }
            }
            case QUEEN -> {
                seenFields.addAll(getRookSeenFields(pieceModel));
                seenFields.addAll(getBishopSeenFields(pieceModel));
            }
            case ROOK -> seenFields.addAll(getRookSeenFields(pieceModel));
            case KNIGHT -> {
                for (int[] offset : knightOffsets) {
                    if (isPositionOnBoard(rank + offset[0], file + offset[1])) {
                        seenFields.add(new Position(rank + offset[0], file + offset[1]));
                    }
                }
            }
            case BISHOP -> seenFields.addAll(getBishopSeenFields(pieceModel));
            case PAWN -> {
                if (file > 0) {
                    seenFields.add(new Position(rank + pieceModel.side().getDirection(), file - 1));
                }
                if (file < 7) {
                    seenFields.add(new Position(rank + pieceModel.side().getDirection(), file + 1));
                }
            }
        }
        return seenFields;
    }


    private Set<Position> getRookSeenFields(PieceModel pieceModel) {
        Set<Position> seenFields = new HashSet<>();
        int rank = pieceModel.getPosition().rank();
        int file = pieceModel.getPosition().file();

        for (int nextFile = file - 1; nextFile >= 0; nextFile--) {
            seenFields.add(new Position(rank, nextFile));
            if (tileModels[nextFile][rank].isFull()) {
                break;
            }
        }
        for (int nextFile = file + 1; nextFile < 8; nextFile++) {
            seenFields.add(new Position(rank, nextFile));
            if (tileModels[nextFile][rank].isFull()) {
                break;
            }
        }
        for (int nextRank = rank - 1; nextRank >= 0; nextRank--) {
            seenFields.add(new Position(nextRank, file));
            if (tileModels[file][nextRank].isFull()) {
                break;
            }
        }
        for (int nextRank = rank + 1; nextRank < 8; nextRank++) {
            seenFields.add(new Position(nextRank, file));
            if (tileModels[file][nextRank].isFull()) {
                break;
            }
        }

        return seenFields;
    }

    private Set<Position> getBishopSeenFields(PieceModel pieceModel) {
        Set<Position> seenFields = new HashSet<>();
        int rank = pieceModel.getPosition().rank();
        int file = pieceModel.getPosition().file();

        for (int nextFile = file - 1, nextRank = rank - 1; isPositionOnBoard(nextRank, nextFile); nextFile--, nextRank--) {
            seenFields.add(new Position(nextRank, nextFile));
            if (tileModels[nextFile][nextRank].isFull()) {
                break;
            }
        }
        for (int nextFile = file + 1, nextRank = rank - 1; isPositionOnBoard(nextRank, nextFile); nextFile++, nextRank--) {
            seenFields.add(new Position(nextRank, nextFile));
            if (tileModels[nextFile][nextRank].isFull()) {
                break;
            }
        }
        for (int nextFile = file - 1, nextRank = rank + 1; isPositionOnBoard(nextRank, nextFile); nextFile--, nextRank++) {
            seenFields.add(new Position(nextRank, nextFile));
            if (tileModels[nextFile][nextRank].isFull()) {
                break;
            }
        }
        for (int nextFile = file + 1, nextRank = rank + 1; isPositionOnBoard(nextRank, nextFile); nextFile++, nextRank++) {
            seenFields.add(new Position(nextRank, nextFile));
            if (tileModels[nextFile][nextRank].isFull()) {
                break;
            }
        }
        return seenFields;
    }

    private boolean isPositionOnBoard(int rank, int file) {
        return rank >= 0 && rank < 8 && file >= 0 && file < 8;
    }

    private List<Position> getPositionsOfPieceType(Sides side, PieceType pieceType) {
        List<Position> positions = new ArrayList<>();
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                PieceModel pieceModel = tileModels[file][rank].getPieceModel();
                if (tileModels[file][rank].isFull() && pieceModel.pieceType() == pieceType && pieceModel.side() == side) {
                    positions.add(pieceModel.getPosition());
                }
            }
        }
        return positions;
    }

    private boolean hasNotMoved(PieceModel pieceModel) {
        for (Move move : moveHistory) {
            for (PieceModel piece : move.getMovedPieces()) {
                if (piece == pieceModel) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isInCheck(Sides side) {
        return getAllSeenFields(side.other()).containsAll(getPositionsOfPieceType(side, PieceType.KING));
    }

    public ListProperty<Move> getMoveHistory() {
        return moveHistory;
    }
}