package com.andbur.models.moves;

import com.andbur.models.*;

import java.util.ArrayList;
import java.util.List;

public class CastleMove extends Move {

    public static final Position[] KING_DESTINATIONS = {new Position(0, 2), new Position(0, 6), new Position(7, 2), new Position(7, 6)};
    public static final Position[] ROOK_POSITIONS = {new Position(0, 0), new Position(0, 7), new Position(7, 0), new Position(7, 7)};
    public static final Position[] ROOK_DESTINATIONS = {new Position(0, 3), new Position(0, 5), new Position(7, 3), new Position(7, 5)};

    private Position rookStartPos;
    private Position rookDestinationPos;

    private PieceModel rookModel = null;

    public CastleMove(TileModel[][] tiles, PieceModel pieceModel, Position destination) {
        super(tiles, pieceModel, destination, null);
        if (pieceModel.pieceType() != PieceType.KING) {
            throw new AssertionError("Expected King, got " + pieceModel.pieceType());
        }
    }

    @Override
    public void execute() {
        super.execute();
        boolean validPosition = false;
        for (int i = 0; i < KING_DESTINATIONS.length; i++) {
            if (KING_DESTINATIONS[i].equals(destinationPos)) {
                rookStartPos = ROOK_POSITIONS[i];
                rookDestinationPos = ROOK_DESTINATIONS[i];
                rookModel = tiles[rookStartPos.file()][rookStartPos.rank()].getPieceModel();
                if (rookModel == null) {
                    throw new AssertionError("Expected Rook, got null");
                }
                if (rookModel.pieceType() != PieceType.ROOK) {
                    throw new AssertionError("Expected Rook, got " + rookModel.pieceType());
                }
                tiles[rookStartPos.file()][rookStartPos.rank()].setPieceModel(null);
                tiles[rookDestinationPos.file()][rookDestinationPos.rank()].setPieceModel(rookModel);
                pieceModel.setPosition(destinationPos);
                validPosition = true;
                break;
            }
        }
        if (!validPosition) {
            throw new AssertionError("Invalid castling destination " + destinationPos);
        }
    }

    @Override
    public void undo() {
        super.undo();
        if(rookModel.getPosition() != rookDestinationPos){
            throw new AssertionError("Rook in wrong position to undo castle: "+rookModel.getPosition());
        }
        tiles[rookDestinationPos.file()][rookDestinationPos.rank()].setPieceModel(null);
        tiles[rookStartPos.file()][rookStartPos.rank()].setPieceModel(rookModel);
        rookModel.setPosition(rookStartPos);
    }

    @Override
    public List<PieceModel> getMovedPieces() {
        ArrayList<PieceModel> pieces = new ArrayList<>(super.getMovedPieces());
        pieces.add(rookModel);
        return pieces;
    }
}
