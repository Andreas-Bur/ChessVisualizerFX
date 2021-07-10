package com.andbur.models.moves;

import com.andbur.models.PieceModel;
import com.andbur.models.Position;
import com.andbur.models.TileModel;

import java.util.ArrayList;
import java.util.List;

public class PromotionMove extends Move {
    final PieceModel promotedPieceModel;

    public PromotionMove(TileModel[][] tiles, PieceModel pieceModel, Position destination, PieceModel pieceTaken, PieceModel promotedPiece) {
        super(tiles, pieceModel, destination, pieceTaken);
        this.promotedPieceModel = promotedPiece;
        this.isCompleteMove = promotedPiece != null;
    }

    public PieceModel getPromotedPieceModel() {
        return promotedPieceModel;
    }

    @Override
    public void execute() {
        super.execute();
        if (promotedPieceModel != null) {
            promotedPieceModel.setPosition(destinationPos);
            tiles[destinationPos.file()][destinationPos.rank()].setPieceModel(promotedPieceModel);
        }
    }

    @Override
    public void undo() {
        super.undo();
    }

    @Override
    public List<PieceModel> getMovedPieces() {
        ArrayList<PieceModel> pieces = new ArrayList<>(super.getMovedPieces());
        pieces.add(promotedPieceModel);
        return pieces;
    }
}
