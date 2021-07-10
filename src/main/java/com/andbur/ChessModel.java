package com.andbur;

import com.andbur.models.Position;
import com.andbur.models.TileModel;

public class ChessModel {
    private final TileModel[][] tileModels = new TileModel[8][8];

    public ChessModel() {
        for(int rank = 0; rank < 8; rank++){
            for(int file = 0; file < 8; file++){
                tileModels[file][rank] = new TileModel(new Position(rank, file));
            }
        }
    }

    public TileModel[][] getTileModels() {
        return tileModels;
    }
}
