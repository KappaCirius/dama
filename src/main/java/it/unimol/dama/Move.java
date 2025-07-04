package it.unimol.dama;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta una mossa nel gioco.
 * Contiene le coordinate di partenza e di arrivo e, nel caso di una cattura,
 * quelle della pedina avversaria da rimuovere.
 */
public class Move
{
    private int startRow, startCol;
    private int endRow, endCol;
    private boolean isCapture;
    private int capturedRow, capturedCol;

    /**
     * Costruttore mossa
     * @param startRow
     * @param startCol
     * @param endRow
     * @param endCol
     */
    public Move(int startRow, int startCol, int endRow, int endCol)
    {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.isCapture = false;
    }

    /**
     * Costruttore cattura
     * @param startRow
     * @param startCol
     * @param endRow
     * @param endCol
     * @param capturedRow
     * @param capturedCol
     */
    public Move(int startRow, int startCol, int endRow, int endCol, int capturedRow, int capturedCol)
    {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.isCapture = true;
        this.capturedRow = capturedRow;
        this.capturedCol = capturedCol;
    }

    /**
     * Metodo per il get della riga di partenza
     * @return startRow
     */
    public int getStartRow() { return startRow; }

    /**
     * Metodo per il get della colonna di partenza
     * @return startCol
     */
    public int getStartCol() { return startCol; }

    /**
     * Metodo per il get della riga di fine
     * @return endRow
     */
    public int getEndRow() { return endRow; }

    /**
     * Metodo per il get della colonna di fine
     * @return endCol
     */
    public int getEndCol() { return endCol; }

    /**
     * Metodo che restituisce il valore isCapture del pezzo
     * @return isCapture
     */
    public boolean isCapture() { return isCapture; }

    /**
     * Metodo che restituisce il valore della riga del pezzo catturato
     * @return capturedRow
     */
    public int getCapturedRow() { return capturedRow; }

    /**
     * Metodo che restituisce il valore della colonna del pezzo catturato
     * @return capturedRCol
     */
    public int getCapturedCol() { return capturedCol; }

    /**
     * Restituisce le mosse possibili
     * @param board
     * @param row
     * @param col
     * @return moves
     */
    public static List<Move> getPossibleMoves(Board board, int row, int col)
    {
        List<Move> moves = new ArrayList<>();
        Piece piece = board.getPiece(row, col);

        if (piece == null)
            return moves;

        int direction = piece.isWhite() ? -1 : 1;
        int[] dirCols = {-1, 1};
        int[] dirRows = piece.isKing() ? new int[]{-1, 1} : new int[]{direction};

        //Mosse
        for (int dr : dirRows)
            for (int dc : dirCols)
            {
                int newRow = row + dr;
                int newCol = col + dc;

                if (isValidMove(board, row, col, newRow, newCol))
                    moves.add(new Move(row, col, newRow, newCol));
            }

        //Cattura
        for (int dr : dirRows)
            for (int dc : dirCols)
            {
                int midRow = row + dr;
                int midCol = col + dc;
                int newRow = row + 2 * dr;
                int newCol = col + 2 * dc;

                if (isValidCapture(board, row, col, midRow, midCol, newRow, newCol))
                    moves.add(new Move(row, col, newRow, newCol, midRow, midCol));
            }

        return moves;
    }

    /**
     * Metodo che controlla se una mossa effettuata è valida o no
     * @param board
     * @param row
     * @param col
     * @param newRow
     * @param newCol
     * @return true se la mossa è valida, false altrimenti
     */
    private static boolean isValidMove(Board board, int row, int col, int newRow, int newCol)
    {
        if (newRow < 0 || newRow >= Board.SIZE || newCol < 0 || newCol >= Board.SIZE)
            return false;

        if (board.getPiece(newRow, newCol) != null)
            return false;

        return true;
    }

    /**
     * Metodo che controlla se la cattura di un pezzo è valida
     * @param board
     * @param row
     * @param col
     * @param midRow
     * @param midCol
     * @param newRow
     * @param newCol
     * @return true se la cattura è valida, false altrimenti
     */
    private static boolean isValidCapture(Board board, int row, int col, int midRow, int midCol, int newRow, int newCol)
    {
        if (newRow < 0 || newRow >= Board.SIZE || newCol < 0 || newCol >= Board.SIZE)
            return false;

        if (board.getPiece(newRow, newCol) != null)
            return false;

        Piece piece = board.getPiece(row, col);
        Piece middlePiece = board.getPiece(midRow, midCol);

        if (middlePiece == null)
            return false;

        if (middlePiece.isWhite() == piece.isWhite())
            return false;

        return true;
    }
}
