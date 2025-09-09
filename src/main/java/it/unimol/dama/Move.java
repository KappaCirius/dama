package it.unimol.dama;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una mossa nel gioco della dama.
 * Contiene coordinate di partenza/arrivo e, in caso di cattura,
 * la posizione del pezzo avversario rimosso.
 */
public class Move {

    /** Riga di partenza. */
    private final int startRow;

    /** Colonna di partenza. */
    private final int startCol;

    /** Riga di arrivo. */
    private final int endRow;

    /** Colonna di arrivo. */
    private final int endCol;

    /** Indica se la mossa è una cattura. */
    private final boolean isCapture;

    /** Riga del pezzo catturato (solo per cattura). */
    private int capturedRow;

    /** Colonna del pezzo catturato (solo per cattura). */
    private int capturedCol;

    /**
     * Crea una mossa semplice (senza cattura).
     *
     * @param startRowArg riga di partenza
     * @param startColArg colonna di partenza
     * @param endRowArg   riga di arrivo
     * @param endColArg   colonna di arrivo
     */
    public Move(final int startRowArg, final int startColArg,
                final int endRowArg, final int endColArg) {
        this.startRow = startRowArg;
        this.startCol = startColArg;
        this.endRow = endRowArg;
        this.endCol = endColArg;
        this.isCapture = false;
    }

    /**
     * Crea una mossa con cattura.
     *
     * @param startRowArg     riga di partenza
     * @param startColArg     colonna di partenza
     * @param endRowArg       riga di arrivo
     * @param endColArg       colonna di arrivo
     * @param capturedRowArg  riga del pezzo catturato
     * @param capturedColArg  colonna del pezzo catturato
     */
    public Move(final int startRowArg, final int startColArg,
                final int endRowArg, final int endColArg,
                final int capturedRowArg, final int capturedColArg) {
        this.startRow = startRowArg;
        this.startCol = startColArg;
        this.endRow = endRowArg;
        this.endCol = endColArg;
        this.isCapture = true;
        this.capturedRow = capturedRowArg;
        this.capturedCol = capturedColArg;
    }

    /**
     * Restituisce la riga di partenza.
     *
     * @return riga di partenza
     */
    public int getStartRow() {
        return startRow;
    }

    /**
     * Restituisce la colonna di partenza.
     *
     * @return colonna di partenza
     */
    public int getStartCol() {
        return startCol;
    }

    /**
     * Restituisce la riga di arrivo.
     *
     * @return riga di arrivo
     */
    public int getEndRow() {
        return endRow;
    }

    /**
     * Restituisce la colonna di arrivo.
     *
     * @return colonna di arrivo
     */
    public int getEndCol() {
        return endCol;
    }

    /**
     * Indica se la mossa è una cattura.
     *
     * @return {@code true} se è cattura, altrimenti {@code false}
     */
    public boolean isCapture() {
        return isCapture;
    }

    /**
     * Restituisce la riga del pezzo catturato.
     *
     * @return riga del pezzo catturato
     */
    public int getCapturedRow() {
        return capturedRow;
    }

    /**
     * Restituisce la colonna del pezzo catturato.
     *
     * @return colonna del pezzo catturato
     */
    public int getCapturedCol() {
        return capturedCol;
    }

    /**
     * Calcola le mosse possibili per il pezzo in (row, col).
     *
     * @param board scacchiera
     * @param row   riga del pezzo
     * @param col   colonna del pezzo
     * @return lista di mosse possibili
     */
    public static List<Move> getPossibleMoves(final Board board,
                                              final int row,
                                              final int col) {
        List<Move> moves = new ArrayList<>();
        Piece piece = board.getPiece(row, col);

        if (piece == null) {
            return moves;
        }

        int direction = piece.isWhite() ? -1 : 1;
        int[] dirCols = new int[] {-1, 1};
        int[] dirRows = piece.isKing()
                ? new int[] {-1, 1}
                : new int[] {direction};

        // Mosse semplici
        for (int dr : dirRows) {
            for (int dc : dirCols) {
                int newRow = row + dr;
                int newCol = col + dc;

                if (isValidMove(board, newRow, newCol)) {
                    moves.add(new Move(row, col, newRow, newCol));
                }
            }
        }

        // Catture
        for (int dr : dirRows) {
            for (int dc : dirCols) {
                int midRow = row + dr;
                int midCol = col + dc;
                int newRow = row + 2 * dr;
                int newCol = col + 2 * dc;

                if (isValidCapture(board, row, col, midRow, midCol,
                        newRow, newCol)) {
                    moves.add(new Move(row, col, newRow, newCol,
                            midRow, midCol));
                }
            }
        }

        return moves;
    }

    /**
     * Verifica se la mossa verso (newRow, newCol) è valida.
     *
     * @param board  scacchiera
     * @param newRow riga di arrivo
     * @param newCol colonna di arrivo
     * @return {@code true} se valida, altrimenti {@code false}
     */
    private static boolean isValidMove(final Board board,
                                       final int newRow,
                                       final int newCol) {
        if (newRow < 0 || newRow >= Board.SIZE
                || newCol < 0 || newCol >= Board.SIZE) {
            return false;
        }

        if (board.getPiece(newRow, newCol) != null) {
            return false;
        }

        return true;
    }

    /**
     * Verifica se la cattura è valida.
     *
     * @param board  scacchiera
     * @param row    riga del pezzo
     * @param col    colonna del pezzo
     * @param midRow riga del pezzo intermedio
     * @param midCol colonna del pezzo intermedio
     * @param newRow riga di arrivo
     * @param newCol colonna di arrivo
     * @return {@code true} se valida, altrimenti {@code false}
     */
    private static boolean isValidCapture(final Board board,
                                          final int row, final int col,
                                          final int midRow, final int midCol,
                                          final int newRow, final int newCol) {
        if (newRow < 0 || newRow >= Board.SIZE
                || newCol < 0 || newCol >= Board.SIZE) {
            return false;
        }

        if (board.getPiece(newRow, newCol) != null) {
            return false;
        }

        Piece piece = board.getPiece(row, col);
        Piece middlePiece = board.getPiece(midRow, midCol);

        if (middlePiece == null) {
            return false;
        }

        return middlePiece.isWhite() != piece.isWhite();
    }
}
