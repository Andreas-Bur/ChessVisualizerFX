package com.andbur.gui;

import javafx.geometry.*;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;

public class ControlPanel extends HBox {
    private final Button resetButton;
    private final Button resignButton;
    private final Button rotateButton;
    private final Button undoButton;
    private final Button redoButton;

    public ControlPanel() {
        resetButton = new Button("Reset Board");
        resignButton = new Button("Resign");
        rotateButton = new Button("Rotate Board");
        undoButton = new Button("Undo");
        redoButton = new Button("Redo");

        Separator separator1 = new Separator();
        separator1.setOrientation(Orientation.VERTICAL);
        separator1.setVisible(false);

        Separator separator2 = new Separator();
        separator2.setOrientation(Orientation.VERTICAL);
        separator2.setVisible(false);

        setSpacing(5);
        setPadding(new Insets(5));
        setAlignment(Pos.CENTER);

        getChildren().addAll(resetButton, resignButton, separator1, rotateButton, separator2, undoButton, redoButton);
    }

    public Button getResetButton() {
        return resetButton;
    }

    public Button getResignButton() {
        return resignButton;
    }

    public Button getRotateButton() {
        return rotateButton;
    }

    public Button getUndoButton() {
        return undoButton;
    }

    public Button getRedoButton() {
        return redoButton;
    }
}
