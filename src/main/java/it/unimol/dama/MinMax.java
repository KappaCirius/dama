package it.unimol.dama;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che implementa l’IA utilizzando l’algoritmo minimax con alpha-beta pruning
 * per ottimizzare la ricerca.
 */
public class MinMax
{
    private static final int MAX_DEPTH = 3;

    /**
     * Restituisce la mossa migliore per l'IA
     * @param controller
     * @return minimaxDecision(controller.getBoard(), MAX_DEPTH, false)
     */
    public static Move getBestMove(Controller controller) { return minimaxDecision(controller.getBoard(), MAX_DEPTH, false); }

    /**
     * Metodo minimax che utilizza l'IA per scegliere la mossa migliore da eseguire
     * @param board
     * @param depth
     * @param whiteTurn
     * @return bestMove
     */
    private static Move minimaxDecision(Board board, int depth, boolean whiteTurn)
    {
        List<Move> moves = getAllMoves(board, whiteTurn);
        Move bestMove = null;

        int bestValue = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (Move move : moves)
        {
            Board newBoard = board.clone();
            Piece piece = newBoard.getPiece(move.getStartRow(), move.getStartCol());
            newBoard.setPiece(move.getEndRow(), move.getEndCol(), piece);
            newBoard.setPiece(move.getStartRow(), move.getStartCol(), null);

            if (move.isCapture())
                newBoard.setPiece(move.getCapturedRow(), move.getCapturedCol(), null);

            int boardValue = minimax(newBoard, depth - 1, !whiteTurn, alpha, beta);

            if (whiteTurn)
            {
                if (boardValue < bestValue)
                {
                    bestValue = boardValue;
                    bestMove = move;
                }

                beta = Math.min(beta, bestValue);
            }
            else
            {
                if (boardValue > bestValue)
                {
                    bestValue = boardValue;
                    bestMove = move;
                }

                alpha = Math.max(alpha, bestValue);
            }

            if (beta <= alpha)
                break;
        }

        return bestMove;
    }

    /**
     * Metodo che utilizza l'IA con l'algoritmo minimax alpha-beta pruning
     * @param board
     * @param depth
     * @param whiteTurn
     * @param alpha
     * @param beta
     * @return value
     */
    private static int minimax(Board board, int depth, boolean whiteTurn, int alpha, int beta)
    {
        if (depth == 0)
            return evaluateBoard(board);

        List<Move> moves = getAllMoves(board, whiteTurn);

        if (moves.isEmpty())
            return whiteTurn ? Integer.MAX_VALUE : Integer.MIN_VALUE;

        int value = Integer.MIN_VALUE;

        for (Move move : moves)
        {
            Board newBoard = board.clone();
            Piece piece = newBoard.getPiece(move.getStartRow(), move.getStartCol());
            newBoard.setPiece(move.getEndRow(), move.getEndCol(), piece);
            newBoard.setPiece(move.getStartRow(), move.getStartCol(), null);

            if (move.isCapture())
                newBoard.setPiece(move.getCapturedRow(), move.getCapturedCol(), null);

            value = Math.max(value, minimax(newBoard, depth - 1, !whiteTurn, alpha, beta));
            alpha = Math.max(alpha, value);

            if (beta <= alpha)
                break;
        }

        return value;

    }

    /**
     * Metodo che utilizza l'IA per valutare la scacchiera dal punto di vista del giocatore nero
     * @param board
     * @return value
     */
    private static int evaluateBoard(Board board)
    {
        int value = 0;

        for (int i = 0; i < Board.SIZE; i++)
            for (int j = 0; j < Board.SIZE; j++)
            {
                Piece p = board.getPiece(i, j);

                if (p != null)
                {
                    int pieceValue = p.isKing() ? 2 : 1;

                    if (p.isWhite())
                        value -= pieceValue;
                    else
                        value += pieceValue;
                }
            }

        return value;
    }

    /**
     * Metodo che restituisce tutte le mosse possibili e le restituisce
     * @param board
     * @param forWhite
     * @return moves
     */
    private static List<Move> getAllMoves(Board board, boolean forWhite)
    {
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < Board.SIZE; i++)
            for (int j = 0; j < Board.SIZE; j++)
            {
                Piece p = board.getPiece(i, j);

                if (p != null && p.isWhite() == forWhite)
                    moves.addAll(Move.getPossibleMoves(board, i, j));
            }

        List<Move> captureMoves = new ArrayList<>();

        for (Move m : moves)
            if (m.isCapture())
                captureMoves.add(m);

        if (!captureMoves.isEmpty())
            return captureMoves;

        return moves;
    }
}
