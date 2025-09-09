package it.unimol.dama;

import java.util.ArrayList;
import java.util.List;

/**
 * IA basata su Minimax con potatura alpha-beta.
 * Fornisce metodi statici per calcolare la mossa migliore.
 */
public final class MinMax {

    /** Profondità massima della ricerca Minimax. */
    private static final int MAX_DEPTH = 3;

    /** Costruttore privato: classe di utilità con soli metodi statici. */
    private MinMax() {
        // no instances
    }

    /**
     * Restituisce la mossa migliore calcolata dall'IA.
     *
     * @param controller controller del gioco
     *
     * @return mossa migliore trovata oppure null se non esiste
     */
    public static Move getBestMove(final Controller controller) {
        return minimaxDecision(controller.getBoard(), MAX_DEPTH, false);
    }

    /**
     * Esegue la decisione Minimax con alpha-beta sullo stato corrente.
     *
     * @param board     scacchiera di partenza
     * @param depth     profondità massima di ricerca
     * @param whiteTurn true se tocca al bianco
     * @return la mossa ritenuta migliore, o null se nessuna
     */
    private static Move minimaxDecision(final Board board,
                                        final int depth,
                                        final boolean whiteTurn) {
        List<Move> moves = getAllMoves(board, whiteTurn);
        Move bestMove = null;

        int bestValue = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (Move move : moves) {
            Board newBoard = board.clone();
            Piece piece = newBoard.getPiece(move.getStartRow(),
                    move.getStartCol());
            newBoard.setPiece(move.getEndRow(), move.getEndCol(), piece);
            newBoard.setPiece(move.getStartRow(), move.getStartCol(), null);

            if (move.isCapture()) {
                newBoard.setPiece(move.getCapturedRow(),
                        move.getCapturedCol(), null);
            }

            int boardValue = minimax(newBoard, depth - 1, !whiteTurn,
                    alpha, beta);

            if (whiteTurn) {
                // minimizza per il bianco
                if (boardValue < bestValue) {
                    bestValue = boardValue;
                    bestMove = move;
                }
                beta = Math.min(beta, bestValue);
            } else {
                // massimizza per il nero
                if (boardValue > bestValue) {
                    bestValue = boardValue;
                    bestMove = move;
                }
                alpha = Math.max(alpha, bestValue);
            }

            if (beta <= alpha) {
                break;
            }
        }

        return bestMove;
    }

    /**
     * Minimax con potatura alpha-beta.
     *
     * @param board     scacchiera
     * @param depth     profondità residua
     * @param whiteTurn true se tocca al bianco
     * @param alpha     limite inferiore
     * @param beta      limite superiore
     *
     * @return valore della posizione dal punto di vista del nero
     */
    private static int minimax(final Board board,
                               final int depth,
                               final boolean whiteTurn,
                               final int alpha,
                               final int beta) {
        int a = alpha;
        int b = beta;

        if (depth == 0) {
            return evaluateBoard(board);
        }

        List<Move> moves = getAllMoves(board, whiteTurn);

        if (moves.isEmpty()) {
            return whiteTurn ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }

        int value = Integer.MIN_VALUE;

        for (Move move : moves) {
            Board newBoard = board.clone();
            Piece piece = newBoard.getPiece(move.getStartRow(),
                    move.getStartCol());
            newBoard.setPiece(move.getEndRow(), move.getEndCol(), piece);
            newBoard.setPiece(move.getStartRow(), move.getStartCol(), null);

            if (move.isCapture()) {
                newBoard.setPiece(move.getCapturedRow(),
                        move.getCapturedCol(), null);
            }

            value = Math.max(value,
                    minimax(newBoard, depth - 1, !whiteTurn, a, b));
            a = Math.max(a, value);

            if (b <= a) {
                break;
            }
        }

        return value;
    }

    /**
     * Valuta la scacchiera dal punto di vista del nero.
     * I pezzi neri sommano il punteggio, i bianchi lo sottraggono.
     *
     * @param board scacchiera
     *
     * @return valore stimato della posizione
     */
    private static int evaluateBoard(final Board board) {
        int value = 0;

        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                Piece p = board.getPiece(i, j);
                if (p != null) {
                    int pieceValue = p.isKing() ? 2 : 1;
                    if (p.isWhite()) {
                        value -= pieceValue;
                    } else {
                        value += pieceValue;
                    }
                }
            }
        }

        return value;
    }

    /**
     * Restituisce tutte le mosse possibili per il colore richiesto.
     * Se esistono catture, restituisce solo le catture.
     *
     * @param board    scacchiera
     * @param forWhite true per il bianco
     *
     * @return lista delle mosse
     */
    private static List<Move> getAllMoves(final Board board,
                                          final boolean forWhite) {
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                Piece p = board.getPiece(i, j);
                if (p != null && p.isWhite() == forWhite) {
                    moves.addAll(Move.getPossibleMoves(board, i, j));
                }
            }
        }

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
}
