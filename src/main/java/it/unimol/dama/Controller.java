package it.unimol.dama;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che funziona da “cervello” del gioco, gestendo la logica e le regole.
 */
public class Controller
{
    private Board board;
    private boolean whiteTurn; // true: bianco, false: nero

    public Controller()
    {
        board = new Board();
        whiteTurn = true;
    }

    /**
     * Metodo che restituisce la scacchiera
     * @return board
     */
    public Board getBoard() { return board; }

    /**
     * Metodo che restituisce il valore di whiteTurn
     * @return whiteTurn
     */
    public boolean isWhiteTurn() { return whiteTurn; }

    /**
     * Metodo che permette di cambiare il turno da bianco a nero
     */
    public void switchTurn() { whiteTurn = !whiteTurn; }

    /**
     * Metodo che restituisce tutte le mosse possibili
     * @param forWhite
     * @return moves
     */
    public List<Move> getAllPossibleMoves(boolean forWhite)
    {
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < Board.SIZE; i++)
            for (int j = 0; j < Board.SIZE; j++)
            {
                Piece p = board.getPiece(i, j);

                if (p != null && p.isWhite() == forWhite)
                    moves.addAll(Move.getPossibleMoves(board, i, j));
            }

        //Se è possibile catturare restituisce solo quella
        List<Move> captureMoves = new ArrayList<>();

        for (Move m : moves)
            if (m.isCapture())
                captureMoves.add(m);

        if (!captureMoves.isEmpty())
            return captureMoves;

        return moves;
    }

    /**
     * Metodo che esegue la mossa
     * @param move
     * @return true se la mossa è valida, false altrimenti
     */
    public boolean makeMove(Move move)
    {
        Piece piece = board.getPiece(move.getStartRow(), move.getStartCol());

        if (piece == null)
            return false;

        List<Move> pieceMoves = Move.getPossibleMoves(board, move.getStartRow(), move.getStartCol());

        //Se esiste una cattura, rende possibili solo tutte le catture
        List<Move> allMoves = getAllPossibleMoves(piece.isWhite());
        boolean compulsoryExists = false;

        for (Move m : allMoves)
            if (m.isCapture())
            {
                compulsoryExists = true;
                break;
            }

        if (compulsoryExists)
        {
            List<Move> captureMoves = new ArrayList<>();

            for (Move m : pieceMoves)
                if (m.isCapture())
                    captureMoves.add(m);

            pieceMoves = captureMoves;
        }

        Move validMove = null;

        for (Move m : pieceMoves)
            if (m.getEndRow() == move.getEndRow() && m.getEndCol() == move.getEndCol())
            {
                validMove = m;
                break;
            }

        if (validMove == null)
            return false;

        //Esegue la mossa
        board.setPiece(validMove.getEndRow(), validMove.getEndCol(), piece);
        board.setPiece(validMove.getStartRow(), validMove.getStartCol(), null);

        if (validMove.isCapture())
            board.setPiece(validMove.getCapturedRow(), validMove.getCapturedCol(), null);

        //Promozione
        if (piece.isWhite() && validMove.getEndRow() == 0)
            piece.crown();
        else if (!piece.isWhite() && validMove.getEndRow() == Board.SIZE - 1)
            piece.crown();

        switchTurn();
        return true;
    }

    /**
     * Metodo che verifica se il gioco è finito
     * @return -1 se vince il nero, 1 se vince il bianco, 2 se il gioco è in corso
     */
    public int checkGameOver()
    {
        boolean whiteHasMove = false;
        boolean blackHasMove = false;
        int whitePieces = 0;
        int blackPieces = 0;

        for (int i = 0; i < Board.SIZE; i++)
            for (int j = 0; j < Board.SIZE; j++)
            {
                Piece p = board.getPiece(i, j);

                if (p != null)
                {
                    List<Move> moves = Move.getPossibleMoves(board, i, j);

                    if (p.isWhite())
                    {
                        whitePieces++;

                        if (!moves.isEmpty())
                            whiteHasMove = true;
                    } else {
                        blackPieces++;

                        if (!moves.isEmpty())
                            blackHasMove = true;
                    }
                }
            }

        if (whitePieces == 0 || !whiteHasMove)
            return -1; //Vince il nero

        if (blackPieces == 0 || !blackHasMove)
            return 1;  //Vince il bianco

        return 2; //Gioco in corso
    }
}
