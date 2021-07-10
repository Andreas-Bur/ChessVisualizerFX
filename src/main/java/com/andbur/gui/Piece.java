package com.andbur.gui;

import com.andbur.models.PieceModel;
import javafx.beans.binding.Bindings;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Piece extends StackPane {
    private final PieceModel pieceModel;
    private Text text;

    public Piece(PieceModel pieceModel, Image pieceImage) {
        this.pieceModel = pieceModel;

        setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        ImageView imageView = new ImageView(pieceImage);

        //getChildren().add(imageView);
        text = new Text(pieceModel.getLabelText());
        fitFontSize();
        widthProperty().addListener(observable -> fitFontSize());
        getChildren().add(text);
    }

    private void fitFontSize() {
        text.setFont(Font.font(getWidth() / 4.0));
    }

    public PieceModel getPieceModel() {
        return pieceModel;
    }
}
