package it.unimol.dama;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce la logica della partita e coordina le regole di gioco.
 */
public class Controller {

    /** Scacchiera corrente. */
    private Board board;

    /** Indica se è il turno del bianco. */
    private boolean whiteTurn; // true: bianco, false: nero

    /** Crea un controller con scacchiera inizializzata e turno al bianco. */
    public Controller() {
        board = new Board();
        whiteTurn = true;
    }

    /**
     * Restituisce la scacchiera.
     *
     * @return scacchiera corrente
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Indica se è il turno del bianco.
     *
     * @return true se tocca al bianco, altrimenti false
     */
    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    /** Cambia il turno. */
    public void switchTurn() {
        whiteTurn = !whiteTurn;
    }

    /**
     * Calcola tutte le mosse possibili per il colore indicato.
     * Se esistono catture, restituisce solo le catture.
     *
     * @param forWhite true per le mosse del bianco
     *
     * @return lista delle mosse possibili
     */
    public List<Move> getAllPossibleMoves(final boolean forWhite) {
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                Piece p = board.getPiece(i, j);
                if (p != null && p.isWhite() == forWhite) {
                    moves.addAll(Move.getPossibleMoves(board, i, j));
                }
            }
        }

        // Se esiste almeno una cattura, le catture sono obbligatorie
        List<Move> captureMoves = new ArrayList<>();
        for (Move m : moves) {
            if (m.isCapture()) {
                captureMoves.add(m);
            }
        }

        if (!captureMoves.isEmpty()) {
            return captureMoves;
        }

        return moves;
    }

    /**
     * Esegue la mossa indicata se è valida per il pezzo selezionato.
     *
     * @param move mossa da eseguire
     * @return true se la mossa è stata eseguita, altrimenti false
     */
    public boolean makeMove(final Move move) {
        Piece piece = board.getPiece(move.getStartRow(), move.getStartCol());
        if (piece == null) {
            return false;
        }

        List<Move> pieceMoves = Move.getPossibleMoves(
                board, move.getStartRow(), move.getStartCol());

        // Se esiste una cattura per il colore del pezzo, rendi obbligatorie
        // solo le catture per quel pezzo.
        List<Move> allMoves = getAllPossibleMoves(piece.isWhite());
        boolean compulsoryExists = false;
        for (Move m : allMoves) {
            if (m.isCapture()) {
                compulsoryExists = true;
                break;
            }
        }
        if (compulsoryExists) {
            List<Move> captureMoves = new ArrayList<>();
            for (Move m : pieceMoves) {
                if (m.isCapture()) {
                    captureMoves.add(m);
                }
            }
            pieceMoves = captureMoves;
        }

        Move validMove = null;
        for (Move m : pieceMoves) {
            if (m.getEndRow() == move.getEndRow()
                    && m.getEndCol() == move.getEndCol()) {
                validMove = m;
                break;
            }
        }

        if (validMove == null) {
            return false;
        }

        // Esegue la mossa sulla scacchiera
        board.setPiece(validMove.getEndRow(), validMove.getEndCol(), piece);
        board.setPiece(validMove.getStartRow(), validMove.getStartCol(), null);

        if (validMove.isCapture()) {
            board.setPiece(validMove.getCapturedRow(),
                    validMove.getCapturedCol(), null);
        }

        // Promozione a dama
        if (piece.isWhite() && validMove.getEndRow() == 0) {
            piece.crown();
        } else if (!piece.isWhite()
                && validMove.getEndRow() == Board.SIZE - 1) {
            piece.crown();
        }

        switchTurn();
        return true;
    }

    /**
     * Verifica se la partita è terminata.
     *
     * @return -1 se vince il nero, 1 se vince il bianco, 2 se il gioco continua
     */
    public int checkGameOver() {
        boolean whiteHasMove = false;
        boolean blackHasMove = false;
        int whitePieces = 0;
        int blackPieces = 0;

        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                Piece p = board.getPiece(i, j);
                if (p != null) {
                    List<Move> moves = Move.getPossibleMoves(board, i, j);
                    if (p.isWhite()) {
                        whitePieces++;
                        if (!moves.isEmpty()) {
                            whiteHasMove = true;
                        }
                    } else {
                        blackPieces++;
                        if (!moves.isEmpty()) {
                            blackHasMove = true;
                        }
                    }
                }
            }
        }

        if (whitePieces == 0 || !whiteHasMove) {
            // Vince il nero
            return -1;
        }

        if (blackPieces == 0 || !blackHasMove) {
            // Vince il bianco
            return 1;
        }

        // Gioco in corso
        return 2;
    }
}
