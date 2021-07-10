package com.andbur.models;

public enum Sides {
    WHITE,
    BLACK;

    public Sides other(){
        return this == WHITE ? BLACK : WHITE;
    }

    public int getDirection(){
        return this == WHITE ? 1 : -1;
    }
}
