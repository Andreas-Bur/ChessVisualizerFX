package com.andbur;

import com.andbur.gui.Piece;
import com.andbur.gui.PromotionPanel;
import com.andbur.gui.Tile;
import com.andbur.models.*;
import com.andbur.models.moves.Move;
import com.andbur.models.moves.PromotionMove;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.util.Set;

public class ChessController {

    private ChessModel chessModel;
    private ChessView chessView;
    private MoveController moveController;

    private boolean canMovePiece = false;

    private final ObjectProperty<GameState> gameState;

    private final ListProperty<Move> undoneMoveList = new SimpleListProperty<>(FXCollections.observableArrayList());

    public ChessController() {
        gameState = new SimpleObjectProperty<>(GameState.UNINITIALIZED);
    }

    private void hookView() {
        chessView.getControlPanel().getResetButton().disableProperty().bind(Bindings.equal(GameState.RUNNING, gameState));
        chessView.getControlPanel().getResignButton().disableProperty().bind(Bindings.notEqual(GameState.RUNNING, gameState));
        chessView.getControlPanel().getRedoButton().disableProperty().bind(undoneMoveList.emptyProperty());
        chessView.getControlPanel().getUndoButton().disableProperty().bind(moveController.getMoveHistory().emptyProperty());

        chessView.getControlPanel().getResignButton().setOnAction(event -> resign());
        chessView.getControlPanel().getResetButton().setOnAction(event -> resetGame());
        chessView.getControlPanel().getRotateButton().setOnAction(event -> chessView.getChessGrid().rotate());
        chessView.getControlPanel().getUndoButton().setOnAction(event -> undoLastMove());
        chessView.getControlPanel().getRedoButton().setOnAction(event -> redoLastMove());
    }

    public ChessModel getChessModel() {
        return chessModel;
    }

    public void setChessModel(ChessModel chessModel) {
        this.chessModel = chessModel;
        this.moveController = new MoveController(chessModel.getTileModels());
    }

    public ChessView getChessView() {
        return chessView;
    }

    public void setChessView(ChessView chessView) {
        this.chessView = chessView;
        if (chessView != null) {
            hookView();
        }
    }

    private Sides getSideAtTurn() {
        return moveController.getMoveHistory().stream().filter(Move::isCompleteMove).count() % 2 == 0 ? Sides.WHITE : Sides.BLACK;
    }

    public boolean hasTurn(Sides side) {
        return moveController.getMoveHistory().stream().filter(Move::isCompleteMove).count() % 2 == side.ordinal();
    }

    private void clearBoard() {
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                chessModel.getTileModels()[file][rank].setPieceModel(null);
            }
        }
    }

    public void prepareGame() {
        initBoard();
        gameState.set(GameState.INITIALIZED);
        canMovePiece = true;
        chessView.refreshTiles();
    }

    private void resetGame() {
        clearBoard();
        moveController.getMoveHistory().clear();
        undoneMoveList.clear();

        prepareGame();
    }

    private void initBoard() {
        TileModel[][] tileModels = chessModel.getTileModels();

        tileModels[0][0].setPieceModel(new PieceModel(PieceType.ROOK, Sides.WHITE, new Position(0, 0)));
        tileModels[1][0].setPieceModel(new PieceModel(PieceType.KNIGHT, Sides.WHITE, new Position(0, 1)));
        tileModels[2][0].setPieceModel(new PieceModel(PieceType.BISHOP, Sides.WHITE, new Position(0, 2)));
        tileModels[3][0].setPieceModel(new PieceModel(PieceType.QUEEN, Sides.WHITE, new Position(0, 3)));
        tileModels[4][0].setPieceModel(new PieceModel(PieceType.KING, Sides.WHITE, new Position(0, 4)));
        tileModels[5][0].setPieceModel(new PieceModel(PieceType.BISHOP, Sides.WHITE, new Position(0, 5)));
        tileModels[6][0].setPieceModel(new PieceModel(PieceType.KNIGHT, Sides.WHITE, new Position(0, 6)));
        tileModels[7][0].setPieceModel(new PieceModel(PieceType.ROOK, Sides.WHITE, new Position(0, 7)));

        tileModels[0][7].setPieceModel(new PieceModel(PieceType.ROOK, Sides.BLACK, new Position(7, 0)));
        tileModels[1][7].setPieceModel(new PieceModel(PieceType.KNIGHT, Sides.BLACK, new Position(7, 1)));
        tileModels[2][7].setPieceModel(new PieceModel(PieceType.BISHOP, Sides.BLACK, new Position(7, 2)));
        tileModels[3][7].setPieceModel(new PieceModel(PieceType.QUEEN, Sides.BLACK, new Position(7, 3)));
        tileModels[4][7].setPieceModel(new PieceModel(PieceType.KING, Sides.BLACK, new Position(7, 4)));
        tileModels[5][7].setPieceModel(new PieceModel(PieceType.BISHOP, Sides.BLACK, new Position(7, 5)));
        tileModels[6][7].setPieceModel(new PieceModel(PieceType.KNIGHT, Sides.BLACK, new Position(7, 6)));
        tileModels[7][7].setPieceModel(new PieceModel(PieceType.ROOK, Sides.BLACK, new Position(7, 7)));

        for (int i = 0; i < 8; i++) {
            tileModels[i][1].setPieceModel(new PieceModel(PieceType.PAWN, Sides.WHITE, new Position(1, i)));
            tileModels[i][6].setPieceModel(new PieceModel(PieceType.PAWN, Sides.BLACK, new Position(6, i)));
        }
    }

    public boolean tryMove(PieceModel pieceModel, Position destination) {
        if (gameState.get() != GameState.INITIALIZED && gameState.get() != GameState.RUNNING) {
            return false;
        }
        if (!canMovePiece) {
            return false;
        }
        Set<Move> potentialMoves = moveController.getPotentialMoves(pieceModel);
        for (Move move : potentialMoves) {
            if (move.getDestinationPos().equals(destination)) {
                if (isInCheckAfterMove(pieceModel, move)) {
                    System.out.println("illegal move: would be in check");
                    return false;
                } else {
                    gameState.set(GameState.RUNNING);
                    doMove(pieceModel, move);
                    if (move.getPieceTaken() != null) {
                        System.out.println("took " + move.getPieceTaken());
                    }
                    return true;
                }
            }
        }
        System.out.println("illegal move");
        return false;
    }

    private boolean isInCheckAfterMove(PieceModel pieceModel, Move move) {
        move.execute();
        boolean inCheck = moveController.isInCheck(pieceModel.side());
        move.undo();

        return inCheck;
    }

    private void doMove(PieceModel pieceModel, Move move) {
        move.execute();

        undoneMoveList.clear();
        moveController.getMoveHistory().add(move);
        int rank = pieceModel.getPosition().rank();
        int file = pieceModel.getPosition().file();

        if (pieceModel.pieceType() == PieceType.PAWN && move.getTiles()[file][rank].getPieceModel() == pieceModel) {
            if ((pieceModel.side() == Sides.BLACK && rank == 0)
                    || (pieceModel.side() == Sides.WHITE && rank == 7)) {
                promotePiece(pieceModel);
            }
        }
        chessView.refreshTiles();
        if (moveController.isInCheck(pieceModel.side().other())) {
            if (isCheckMate(pieceModel.side().other())) {
                gameState.set(GameState.FINISHED);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, pieceModel.side() + " won by checkmate!");
                alert.showAndWait();
            } else {
                System.out.println(pieceModel.side().other() + " in check after move!");
            }
        }
    }

    private void promotePiece(PieceModel pieceModel) {
        canMovePiece = false;
        int file = pieceModel.getPosition().file();
        int rank = pieceModel.getPosition().rank();
        Tile tile = chessView.getTiles()[file][rank];
        PromotionPanel promotionPanel = new PromotionPanel(pieceModel.side(), chessView.getPieceImageMap());
        promotionPanel.setBackground(tile.getBackground());
        promotionPanel.setEffect(new DropShadow(5, Color.GREEN));
        Move move = moveController.getMoveHistory().get(moveController.getMoveHistory().size() - 1);
        for (Piece piece : promotionPanel.getPieces()) {
            piece.setOnMouseClicked(event -> {
                tile.removePromotePanel();
                PromotionMove promotionMove = new PromotionMove(move.getTiles(), pieceModel, move.getDestinationPos(), move.getPieceTaken(), ((Piece) event.getSource()).getPieceModel());
                move.undo();
                moveController.getMoveHistory().remove(move);
                doMove(pieceModel, promotionMove);
                canMovePiece = true;
            });
        }
        tile.showPromotePanel(promotionPanel);
    }

    private boolean isCheckMate(Sides side) {
        Set<Position> piecePositions = moveController.getOccupiedFields(side);
        for (Position pos : piecePositions) {
            PieceModel pieceModel = chessModel.getTileModels()[pos.file()][pos.rank()].getPieceModel();
            Set<Move> moves = moveController.getPotentialMoves(pieceModel);
            for (Move move : moves) {
                if (!isInCheckAfterMove(pieceModel, move)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void resign() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, getSideAtTurn().toString() + ", do your really want to resign?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Resign?");
        var result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            doResign();
        }
    }

    private void doResign() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, getSideAtTurn().other().toString() + " won by resignation");
        alert.setTitle(getSideAtTurn().other().toString() + " won!");
        gameState.set(GameState.FINISHED);
        alert.showAndWait();
    }

    private void undoLastMove() {
        Move move = moveController.getMoveHistory().remove(0);
        move.undo();
        undoneMoveList.add(move);
        chessView.refreshTiles();
    }

    private void redoLastMove() {
        Move move = undoneMoveList.remove(0);
        move.execute();
        moveController.getMoveHistory().add(move);
        chessView.refreshTiles();
    }
}
