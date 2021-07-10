package com.andbur.models.moves;

import com.andbur.models.PieceModel;
import com.andbur.models.Position;
import com.andbur.models.TileModel;

import java.util.List;

public class Move {
    protected final TileModel[][] tiles;
    protected final PieceModel pieceModel;
    protected final Position startPos;
    protected final Position destinationPos;
    protected final PieceModel pieceTaken;
    protected boolean isCompleteMove = true;

    public Move(TileModel[][] tiles, PieceModel pieceModel, Position destination, PieceModel pieceTaken) {
        this.tiles = tiles;
        this.pieceModel = pieceModel;
        this.destinationPos = destination;
        this.pieceTaken = pieceTaken;
        startPos = pieceModel.getPosition();
    }

    public void execute() {
        tiles[pieceModel.getPosition().file()][pieceModel.getPosition().rank()].setPieceModel(null);
        tiles[destinationPos.file()][destinationPos.rank()].setPieceModel(pieceModel);
        pieceModel.setPosition(destinationPos);
    }

    public void undo() {
        if (pieceModel.getPosition() != destinationPos) {
            throw new AssertionError("Wrong position to undo move: pieceModel position" + pieceModel.getPosition()+", destination position: "+pieceModel.getPosition());
        }
        tiles[destinationPos.file()][destinationPos.rank()].setPieceModel(pieceTaken);
        if (pieceTaken != null) {
            pieceTaken.setPosition(destinationPos);
        }
        tiles[startPos.file()][startPos.rank()].setPieceModel(pieceModel);
        pieceModel.setPosition(startPos);
    }

    public List<PieceModel> getMovedPieces() {
        return List.of(pieceModel);
    }

    public Position getStartPos() {
        return startPos;
    }

    public PieceModel getPieceTaken() {
        return pieceTaken;
    }

    public Position getDestinationPos() {
        return destinationPos;
    }

    public boolean isCompleteMove() {
        return isCompleteMove;
    }

    public TileModel[][] getTiles() {
        return tiles;
    }

    public PieceModel getPieceModel() {
        return pieceModel;
    }
}