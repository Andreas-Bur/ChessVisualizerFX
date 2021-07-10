package com.andbur.models.moves;

import com.andbur.models.*;

public class EnPassantMove extends Move {
    Position takenPosition;

    public EnPassantMove(TileModel[][] tiles, PieceModel pieceModel, Position destination, PieceModel pieceTaken) {
        super(tiles, pieceModel, destination, pieceTaken);
        if(pieceModel.pieceType() != PieceType.PAWN || pieceTaken.pieceType() != PieceType.PAWN){
            throw new AssertionError("expected 2 pawns, got "+pieceModel+" and "+pieceTaken);
        }
    }

    @Override
    public void execute() {
        super.execute();
        takenPosition = pieceTaken.getPosition();
        tiles[takenPosition.file()][takenPosition.rank()].setPieceModel(null);
    }

    @Override
    public void undo() {
        super.undo();
        tiles[takenPosition.file()][takenPosition.rank()].setPieceModel(pieceTaken);
        pieceTaken.setPosition(takenPosition);
    }
}
