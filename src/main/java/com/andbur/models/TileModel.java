package com.andbur.models;

public class TileModel {

    private PieceModel pieceModel = null;
    private final Position position;

    public TileModel(Position position) {
        this.position = position;
    }

    public boolean isFull(){
        return pieceModel != null;
    }

    public PieceModel getPieceModel() {
        return pieceModel;
    }

    public void setPieceModel(PieceModel pieceModel) {
        this.pieceModel = pieceModel;
    }

    public Position getPosition() {
        return position;
    }
}
