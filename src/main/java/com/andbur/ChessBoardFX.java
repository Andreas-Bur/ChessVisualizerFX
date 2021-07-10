package com.andbur;

import com.andbur.models.PieceType;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashMap;


/**
 * JavaFX App
 */
public class ChessBoardFX extends Application {
    private final HashMap<PieceType, Image> pieceImageMap = new HashMap<>();


    @Override
    public void start(Stage stage) {
        loadImages();

        ChessModel chessModel = new ChessModel();
        ChessController chessController = new ChessController();
        ChessView chessView = new ChessView(chessModel, chessController, pieceImageMap);

        chessController.setChessModel(chessModel);
        chessController.setChessView(chessView);
        chessController.prepareGame();

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(chessView);

        Scene scene = new Scene(borderPane, 640, 480);
        stage.setScene(scene);
        stage.show();
    }


    private void loadImages() {
        pieceImageMap.put(PieceType.PAWN, loadImage(PieceType.PAWN));
        pieceImageMap.put(PieceType.ROOK, loadImage(PieceType.ROOK));
        pieceImageMap.put(PieceType.KNIGHT, loadImage(PieceType.KNIGHT));
        pieceImageMap.put(PieceType.BISHOP, loadImage(PieceType.BISHOP));
        pieceImageMap.put(PieceType.KING, loadImage(PieceType.KING));
        pieceImageMap.put(PieceType.QUEEN, loadImage(PieceType.QUEEN));
    }

    private Image loadImage(PieceType pieceType) {
        Image image = null;
        /*try (FileInputStream fileInputStream = new FileInputStream("resources/" + pieceType + ".png")) {
            image = new Image(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return image;
    }

    public static void main(String[] args) {
        launch();
    }

}