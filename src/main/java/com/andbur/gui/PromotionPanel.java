package com.andbur.gui;

import com.andbur.models.PieceModel;
import com.andbur.models.PieceType;
import com.andbur.models.Sides;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.HashMap;

public class PromotionPanel extends GridPane {
    private final Piece[] pieces;

    public PromotionPanel(Sides side, HashMap<PieceType, Image> pieceImageMap) {
        pieces = new Piece[4];
        pieces[0] = new Piece(new PieceModel(PieceType.QUEEN, side, null), pieceImageMap.get(PieceType.QUEEN));
        pieces[1] = new Piece(new PieceModel(PieceType.KNIGHT, side, null), pieceImageMap.get(PieceType.KNIGHT));
        pieces[2] = new Piece(new PieceModel(PieceType.ROOK, side, null), pieceImageMap.get(PieceType.ROOK));
        pieces[3] = new Piece(new PieceModel(PieceType.BISHOP, side, null), pieceImageMap.get(PieceType.BISHOP));

        add(pieces[0], 0, 0);
        add(pieces[1], 1, 0);
        add(pieces[2], 0, 1);
        add(pieces[3], 1, 1);

        ColumnConstraints colConst0 = new ColumnConstraints();
        colConst0.setPercentWidth(50);
        ColumnConstraints colConst1 = new ColumnConstraints();
        colConst1.setPercentWidth(50);
        getColumnConstraints().addAll(colConst0, colConst1);

        RowConstraints rowConst0 = new RowConstraints();
        rowConst0.setPercentHeight(50);
        RowConstraints rowConst1 = new RowConstraints();
        rowConst1.setPercentHeight(50);
        getRowConstraints().addAll(rowConst0, rowConst1);
    }

    public Piece[] getPieces() {
        return pieces;
    }
}
