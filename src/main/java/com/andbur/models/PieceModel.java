package com.andbur.models;

import java.util.Objects;

public class PieceModel {
    private final PieceType pieceType;
    private final Sides side;

    private Position position;

    public PieceModel(PieceType pieceType, Sides side, Position position) {
        this.pieceType = pieceType;
        this.side = side;
        this.position = position;
    }

    public PieceType pieceType() {
        return pieceType;
    }

    public Sides side() {
        return side;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PieceModel) obj;
        return Objects.equals(this.pieceType, that.pieceType) &&
                Objects.equals(this.side, that.side);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceType, side);
    }

    @Override
    public String toString() {
        return side+" "+pieceType;
    }

    public String getLabelText(){
        return side+"\n"+pieceType;
    }
}
