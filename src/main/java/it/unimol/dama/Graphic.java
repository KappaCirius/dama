package it.unimol.dama;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce l'interfaccia grafica del gioco tramite JavaFX.
 */
public class Graphic extends Application {

    /** Lato di una singola casella della scacchiera (in px). */
    private static final int TILE_SIZE = 80;

    /** Raggio relativo del cerchio che rappresenta una pedina. */
    private static final double PIECE_RADIUS_FACTOR = 0.35;

    /** Opacità dell'evidenziazione della casella selezionata. */
    private static final double HIGHLIGHT_ALPHA = 0.3;

    /** Raggio relativo del marker per una mossa possibile. */
    private static final double MOVE_MARKER_RADIUS_FACTOR = 0.2;

    /** Opacità del marker per una mossa possibile. */
    private static final double MOVE_MARKER_ALPHA = 0.5;

    /** Controller della logica di gioco. */
    private Controller controller;

    /** Griglia principale con le caselle. */
    private GridPane grid;

    /** Riga attualmente selezionata (oppure -1 se nulla). */
    private int selectedRow = -1;

    /** Colonna attualmente selezionata (oppure -1 se nulla). */
    private int selectedCol = -1;

    /** Mosse disponibili per la pedina selezionata. */
    private List<Move> availableMoves = new ArrayList<>();

    /**
     * Inizializza e mostra la scena della scacchiera.
     *
     * @param primaryStage finestra principale di JavaFX
     */
    @Override
    public void start(final Stage primaryStage) {
        controller = new Controller();
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        drawBoard();

        int size = Board.SIZE * TILE_SIZE;
        Scene scene = new Scene(grid, size, size);
        primaryStage.setTitle("Dama");
        primaryStage.setScene(scene);
        primaryStage.show();

        if (!controller.isWhiteTurn()) {
            Platform.runLater(this::performAIMove);
        }
    }

    /**
     * Disegna (o ridisegna) la scacchiera e i pezzi.
     */
    private void drawBoard() {
        grid.getChildren().clear();

        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                StackPane square = new StackPane();
                Rectangle rect = new Rectangle(TILE_SIZE, TILE_SIZE);

                // Colori delle caselle
                if ((row + col) % 2 == 0) {
                    rect.setFill(Color.WHITE);
                } else {
                    rect.setFill(Color.GREEN);
                }
                square.getChildren().add(rect);

                Piece piece = controller.getBoard().getPiece(row, col);
                if (piece != null) {
                    Circle circle = new Circle(
                            TILE_SIZE * PIECE_RADIUS_FACTOR
                    );
                    circle.setFill(
                            piece.isWhite() ? Color.WHITE : Color.BLACK
                    );
                    circle.setStroke(Color.GRAY);
                    square.getChildren().add(circle);

                    if (piece.isKing()) {
                        Text kingText = new Text("K");
                        kingText.setFill(
                                piece.isWhite() ? Color.BLACK : Color.WHITE
                        );
                        square.getChildren().add(kingText);
                    }
                }

                final int currentRow = row;
                final int currentCol = col;
                square.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        handleClick(currentRow, currentCol);
                    }
                });

                grid.add(square, col, row);
            }
        }

        // Evidenzia la pedina selezionata
        if (selectedRow != -1 && selectedCol != -1) {
            StackPane square = getSquare(selectedRow, selectedCol);
            if (square != null) {
                Rectangle highlight = new Rectangle(TILE_SIZE, TILE_SIZE);
                highlight.setFill(
                        new Color(0, 1, 0, HIGHLIGHT_ALPHA)
                );
                square.getChildren().add(highlight);
            }
        }

        // Marca le mosse disponibili della pedina selezionata
        for (Move move : availableMoves) {
            StackPane square = getSquare(move.getEndRow(), move.getEndCol());
            if (square != null) {
                Circle marker = new Circle(
                        TILE_SIZE * MOVE_MARKER_RADIUS_FACTOR
                );
                marker.setFill(
                        new Color(0, 0, 0, MOVE_MARKER_ALPHA)
                );
                square.getChildren().add(marker);
            }
        }
    }

    /**
     * Gestisce il click su una casella.
     *
     * @param row riga cliccata
     * @param col colonna cliccata
     */
    private void handleClick(final int row, final int col) {
        if (!controller.isWhiteTurn()) {
            return;
        }

        if (selectedRow == -1 && selectedCol == -1) {
            Piece piece = controller.getBoard().getPiece(row, col);
            if (piece != null && piece.isWhite()) {
                selectedRow = row;
                selectedCol = col;
                availableMoves = getAvailableMoves(selectedRow, selectedCol);
                drawBoard();
            }
        } else {
            Move chosenMove = null;
            for (Move move : availableMoves) {
                if (move.getEndRow() == row && move.getEndCol() == col) {
                    chosenMove = move;
                    break;
                }
            }

            if (chosenMove != null) {
                boolean success = controller.makeMove(chosenMove);
                if (success) {
                    availableMoves.clear();
                    selectedRow = -1;
                    selectedCol = -1;
                    drawBoard();

                    int result = controller.checkGameOver();
                    if (result != 2) {
                        showGameOver(result);
                    } else {
                        Platform.runLater(this::performAIMove);
                    }
                }
            } else {
                // Click non valido: reset selezione
                selectedRow = -1;
                selectedCol = -1;
                availableMoves.clear();
                drawBoard();
            }
        }
    }

    /**
     * Calcola le mosse disponibili per la pedina alla posizione indicata.
     * Se esistono catture obbligatorie, restituisce solo le catture.
     *
     * @param row riga della pedina
     * @param col colonna della pedina
     *
     * @return lista delle mosse disponibili
     */
    private List<Move> getAvailableMoves(final int row, final int col) {
        List<Move> moves =
                Move.getPossibleMoves(controller.getBoard(), row, col);

        List<Move> allValidMoves = controller.getAllPossibleMoves(true);
        boolean compulsoryExists = false;

        for (Move m : allValidMoves) {
            if (m.isCapture()) {
                compulsoryExists = true;
                break;
            }
        }

        if (compulsoryExists) {
            List<Move> captureMoves = new ArrayList<>();
            for (Move m : moves) {
                if (m.isCapture()) {
                    captureMoves.add(m);
                }
            }
            return captureMoves;
        }

        return moves;
    }

    /**
     * Restituisce lo StackPane alla posizione (row, col) nella griglia.
     *
     * @param row riga
     * @param col colonna
     *
     * @return lo StackPane trovato oppure null
     */
    private StackPane getSquare(final int row, final int col) {
        for (javafx.scene.Node node : grid.getChildren()) {
            Integer r = GridPane.getRowIndex(node);
            Integer c = GridPane.getColumnIndex(node);
            if (r != null && c != null && r == row && c == col) {
                return (StackPane) node;
            }
        }
        return null;
    }

    /**
     * Esegue la mossa dell'IA se tocca al nero.
     */
    private void performAIMove() {
        if (controller.isWhiteTurn()) {
            return;
        }

        Move aiMove = MinMax.getBestMove(controller);
        if (aiMove != null) {
            controller.makeMove(aiMove);
            drawBoard();

            int result = controller.checkGameOver();
            if (result != 2) {
                showGameOver(result);
            }
        } else {
            showGameOver(1);
        }
    }

    /**
     * Mostra il messaggio di fine partita (vittoria, sconfitta o pareggio).
     *
     * @param result risultato: 1=vittoria b, -1=sconfitta b, 2=continua
     */
    private void showGameOver(final int result) {
        String message;
        if (result == 1) {
            message = "Hai vinto!";
        } else if (result == -1) {
            message = "Hai perso!";
        } else {
            message = "Pareggio!";
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        Platform.exit();
    }
}
