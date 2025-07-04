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
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe che gestisce l’interfaccia grafica del gioco tramite JavaFX.
 */
public class Graphic extends Application
{
    private static final int TILE_SIZE = 80;
    private Controller controller;
    private GridPane grid;
    private int selectedRow = -1, selectedCol = -1;
    private List<Move> availableMoves = new ArrayList<>(); //Mosse disponibili

    /**
     * Metodo che inizializza la scena della scacchiera della Dama
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage)
    {
        controller = new Controller();
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        drawBoard();

        Scene scene = new Scene(grid, Board.SIZE * TILE_SIZE, Board.SIZE * TILE_SIZE);
        primaryStage.setTitle("Dama");
        primaryStage.setScene(scene);
        primaryStage.show();

        if (!controller.isWhiteTurn())
            Platform.runLater(() -> performAIMove());
    }

    /**
     * Metodo che disegna la scacchiera
     */
    private void drawBoard()
    {
        grid.getChildren().clear();

        for (int row = 0; row < Board.SIZE; row++)
        {
            for (int col = 0; col < Board.SIZE; col++)
            {
                StackPane square = new StackPane();
                Rectangle rect = new Rectangle(TILE_SIZE, TILE_SIZE);

                //Imposta i colori delle caselle
                if ((row + col) % 2 == 0)
                    rect.setFill(Color.WHITE);
                else
                    rect.setFill(Color.GREEN);

                square.getChildren().add(rect);

                Piece piece = controller.getBoard().getPiece(row, col);

                if (piece != null)
                {
                    Circle circle = new Circle(TILE_SIZE * 0.35);
                    circle.setFill(piece.isWhite() ? Color.WHITE : Color.BLACK);
                    circle.setStroke(Color.GRAY);
                    square.getChildren().add(circle);

                    if (piece.isKing())
                    {
                        Text kingText = new Text("K");
                        kingText.setFill(piece.isWhite() ? Color.BLACK : Color.WHITE);
                        square.getChildren().add(kingText);
                    }
                }

                final int currentRow = row;
                final int currentCol = col;

                square.setOnMouseClicked(e ->
                {
                    if (e.getButton() == MouseButton.PRIMARY)
                        handleClick(currentRow, currentCol);
                });

                grid.add(square, col, row);
            }
        }

        // Evidenzia la pedina selezionata
        if (selectedRow != -1 && selectedCol != -1)
        {
            StackPane square = getSquare(selectedRow, selectedCol);

            if (square != null)
            {
                Rectangle highlight = new Rectangle(TILE_SIZE, TILE_SIZE);
                highlight.setFill(new Color(0, 1, 0, 0.3));
                square.getChildren().add(highlight);
            }
        }

        //Marca le mosse disponibili della pedina selezionata
        for (Move move : availableMoves)
        {
            StackPane square = getSquare(move.getEndRow(), move.getEndCol());

            if (square != null)
            {
                Circle marker = new Circle(TILE_SIZE * 0.2);
                marker.setFill(new Color(0, 0, 0, 0.5));
                square.getChildren().add(marker);
            }
        }
    }

    /**
     * Metodo per gestire il click
     * @param row
     * @param col
     */
    private void handleClick(int row, int col)
    {
        if (controller.isWhiteTurn())
            if (selectedRow == -1 && selectedCol == -1)
            {
                Piece piece = controller.getBoard().getPiece(row, col);

                if (piece != null && piece.isWhite())
                {
                    selectedRow = row;
                    selectedCol = col;
                    availableMoves = getAvailableMoves(selectedRow, selectedCol);
                    drawBoard();
                }
            }
            else
            {
                Move chosenMove = null;

                for (Move move : availableMoves)
                    if (move.getEndRow() == row && move.getEndCol() == col)
                    {
                        chosenMove = move;
                        break;
                    }
                if (chosenMove != null)
                {
                    boolean success = controller.makeMove(chosenMove);

                    if (success)
                    {
                        availableMoves.clear();
                        selectedRow = -1;
                        selectedCol = -1;
                        drawBoard();

                        int result = controller.checkGameOver();

                        if (result != 2)
                            showGameOver(result);
                        else
                            Platform.runLater(() -> performAIMove());
                    }
                }
                else
                {
                    //Click non corrispondente
                    selectedRow = -1;
                    selectedCol = -1;
                    availableMoves.clear();
                    drawBoard();
                }
            }
    }

    /**
     * Metodo che calcola le possibili mosse eseguibili e le restituisce
     * @param row
     * @param col
     * @return moves
     */
    private List<Move> getAvailableMoves(int row, int col)
    {
        List<Move> moves = Move.getPossibleMoves(controller.getBoard(), row, col);
        List<Move> allValidMoves = controller.getAllPossibleMoves(true);
        boolean compulsoryExists = false;

        for (Move m : allValidMoves)
            if (m.isCapture())
            {
                compulsoryExists = true;
                break;
            }

        if (compulsoryExists)
        {
            List<Move> captureMoves = new ArrayList<>();
            for (Move m : moves)
                if (m.isCapture())
                    captureMoves.add(m);

            moves = captureMoves;
        }

        return moves;
    }

    /**
     * Metodo che recupera lo StackPane alla riga e colonna specificate all'interno della griglia
     * @param row
     * @param col
     * @return (StackPane) node, oppure null
     */
    private StackPane getSquare(int row, int col)
    {
        for (javafx.scene.Node node : grid.getChildren())
        {
            Integer r = GridPane.getRowIndex(node);
            Integer c = GridPane.getColumnIndex(node);

            if (r != null && c != null && r == row && c == col)
                return (StackPane) node;
        }

        return null;
    }

    /**
     * Metodo che effettua l'IA per eseguire le mosse se è il turno del nero
     */
    private void performAIMove()
    {
        if (controller.isWhiteTurn())
            return;

        Move aiMove = MinMax.getBestMove(controller);

        if (aiMove != null)
        {
            controller.makeMove(aiMove);
            drawBoard();

            int result = controller.checkGameOver();

            if (result != 2)
                showGameOver(result);
        }
        else
            showGameOver(1);
    }

    /**
     * Metodo che visualizza a partita finita un messaggio di vittoria, sconfitta o pareggio
     * @param result
     */
    private void showGameOver(int result)
    {
        String message;
        if (result == 1)
            message = "Hai vinto!";
        else if (result == -1)
            message = "Hai perso!";
        else
            message = "Pareggio!";

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        Platform.exit();
    }
}
