package com.andbur.gui;

import com.andbur.models.PieceModel;
import com.andbur.models.Position;
import com.andbur.models.TileModel;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;

public class Tile extends StackPane {

    private PieceModel curPieceModel = null;
    private final Position position;
    private PromotionPanel promotionPanel;

    public Tile(ObjectProperty<Background> bgProperty, Position position) {
        this.position = position;
        setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        backgroundProperty().bind(bgProperty);
    }

    public void setPiece(Piece piece) {
        getChildren().clear();
        if(piece != null){
            this.curPieceModel = piece.getPieceModel();
            getChildren().add(piece);
        } else {
            this.curPieceModel = null;
        }
        if(promotionPanel != null){
            getChildren().add(promotionPanel);
        }
    }

    public void showPromotePanel(PromotionPanel promotionPanel){
        this.promotionPanel = promotionPanel;
    }

    public void removePromotePanel(){
        getChildren().remove(promotionPanel);
        promotionPanel = null;
    }

    public PieceModel getCurPieceModel() {
        return curPieceModel;
    }

    public Position getPosition() {
        return position;
    }
}
