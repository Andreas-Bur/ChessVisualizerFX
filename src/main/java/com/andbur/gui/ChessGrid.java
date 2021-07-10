package com.andbur.gui;

import com.andbur.ChessController;
import com.andbur.models.Position;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ChessGrid extends GridPane {
    private final Label[] rankTexts = new Label[8];
    private final Label[] fileTexts = new Label[8];

    private final ObjectProperty<Background> lightTileBg = new SimpleObjectProperty<>(new Background(new BackgroundFill(Color.BURLYWOOD, null, null)));
    private final ObjectProperty<Background> darkTileBg = new SimpleObjectProperty<>(new Background(new BackgroundFill(Color.SADDLEBROWN, null, null)));

    private final Tile[][] tiles = new Tile[8][8];

    private boolean rotated = false;

    public ChessGrid() {
        for (int x = 1; x <= 8; x++) {
            fileTexts[x - 1] = createRankFileLabel("" + (char) ('a' - 1 + x));
        }
        for (int y = 1; y <= 8; y++) {
            rankTexts[y - 1] = createRankFileLabel("" + y);
        }

        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                ObjectProperty<Background> bgProperty = (x + y) % 2 == 0 ? darkTileBg : lightTileBg;
                tiles[x - 1][y - 1] = new Tile(bgProperty, new Position(y - 1, x - 1));
            }
        }

        refreshComponents();

        ColumnConstraints colConst0 = new ColumnConstraints();
        colConst0.setPercentWidth(100 / 18.0);
        getColumnConstraints().add(colConst0);
        for (int i = 1; i <= 8; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100 / 9.0);
            getColumnConstraints().add(colConst);
        }
        ColumnConstraints colConst9 = new ColumnConstraints();
        colConst9.setPercentWidth(100 / 18.0);
        getColumnConstraints().add(colConst9);

        RowConstraints rowConst0 = new RowConstraints();
        rowConst0.setPercentHeight(100 / 18.0);
        getRowConstraints().add(rowConst0);
        for (int i = 1; i <= 8; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100 / 9.0);
            getRowConstraints().add(rowConst);
        }
        RowConstraints rowConst9 = new RowConstraints();
        rowConst9.setPercentHeight(100 / 18.0);
        getRowConstraints().add(rowConst9);

        setMinSize(200, 200);
    }

    public void setChessControllerHooks(ChessController chessController) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Tile tile = tiles[x][y];
                tile.setOnMouseDragReleased(event -> {
                    if (event.getGestureSource() instanceof Piece piece) {
                        if (chessController.tryMove(piece.getPieceModel(), tile.getPosition())) {
                            System.out.println("released " + piece.getPieceModel().pieceType());
                        }
                    }
                });
            }
        }
    }

    private void refreshComponents(){
        getChildren().removeAll(fileTexts);
        getChildren().removeAll(rankTexts);
        for (int i = 0; i < 8; i++) {
            add(fileTexts[i], i+1, 0);
            add(rankTexts[i], 0, rotated ? i+1 : 8-i);
            getChildren().removeAll(tiles[i]);
            for (int j = 0; j < 8; j++) {
                add(tiles[i][j], i+1, rotated ? j+1 : 8-j);
            }
        }
    }

    private Label createRankFileLabel(String text) {
        Label label = new Label(text);
        label.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    public Label[] getRankTexts() {
        return rankTexts;
    }

    public Label[] getFileTexts() {
        return fileTexts;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public void rotate() {
        rotated = !rotated;
        refreshComponents();
    }
}
