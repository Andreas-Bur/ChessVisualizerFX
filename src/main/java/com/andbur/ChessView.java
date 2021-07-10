package com.andbur;

import com.andbur.gui.ChessGrid;
import com.andbur.gui.ControlPanel;
import com.andbur.gui.Piece;
import com.andbur.gui.Tile;
import com.andbur.models.PieceModel;
import com.andbur.models.PieceType;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashMap;

public class ChessView extends StackPane {
    private ChessModel chessModel;
    private ChessController chessController;

    private final ChessGrid chessGrid;
    private final ControlPanel controlPanel;
    private final HashMap<PieceType, Image> pieceImageMap;

    private final NumberBinding gridSize = Bindings.min(widthProperty(), heightProperty());

    public ChessView(ChessModel chessModel, ChessController chessController, HashMap<PieceType, Image> pieceImageMap) {
        this.chessModel = chessModel;
        this.chessController = chessController;
        this.pieceImageMap = pieceImageMap;

        chessGrid = new ChessGrid();
        chessGrid.setChessControllerHooks(chessController);
        chessGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        controlPanel = new ControlPanel();

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(chessGrid);
        borderPane.setBottom(controlPanel);

        chessGrid.prefHeightProperty().bind(gridSize);
        chessGrid.prefWidthProperty().bind(gridSize);
        chessGrid.maxHeightProperty().bind(gridSize);
        chessGrid.maxWidthProperty().bind(gridSize);
        chessGrid.widthProperty().addListener((observable, oldValue, newValue) -> resizeFont());
        resizeFont();

        getChildren().add(borderPane);
    }

    private void resizeFont() {
        Font font = Font.font("Verdana", FontWeight.BOLD, gridSize.getValue().intValue() / 30.0);
        for (int i = 0; i < 8; i++) {
            chessGrid.getRankTexts()[i].setFont(font);
            chessGrid.getFileTexts()[i].setFont(font);
        }
    }

    public void refreshTiles() {
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                if (chessModel.getTileModels()[file][rank].isFull()) {
                    PieceModel pieceModel = chessModel.getTileModels()[file][rank].getPieceModel();
                    if (chessGrid.getTiles()[file][rank].getCurPieceModel() != pieceModel) {
                        chessGrid.getTiles()[file][rank].setPiece(createPiecePanel(pieceModel));
                    }
                } else {
                    chessGrid.getTiles()[file][rank].setPiece(null);
                }
            }
        }
    }

    private Piece createPiecePanel(PieceModel pieceModel) {
        Piece piece = new Piece(pieceModel, pieceImageMap.get(pieceModel.pieceType()));
        piece.setOnDragDetected(event -> {
            if (chessController.hasTurn(pieceModel.side())) {
                piece.startFullDrag();
            }
        });
        return piece;
    }

    public HashMap<PieceType, Image> getPieceImageMap() {
        return pieceImageMap;
    }

    public Tile[][] getTiles() {
        return chessGrid.getTiles();
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public ChessGrid getChessGrid() {
        return chessGrid;
    }
}
