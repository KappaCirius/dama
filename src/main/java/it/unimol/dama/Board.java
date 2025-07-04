package it.unimol.dama;

/**
 * Classe che rappresenta la scacchiera del gioco della dama.
 */
public class    Board {
    public static final int SIZE = 8;
    private Piece[][] board;

    /**
     * Costruttore che inizializza la scacchiera.
     */
    public Board() {
        board = new Piece[SIZE][SIZE];
        initializeBoard();
    }

    /**
     * Inizializza la scacchiera posizionando le pedine nei posti iniziali.
     */
    public void initializeBoard() {
        // Posiziona le pedine sulla scacchiera
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if ((row + col) % 2 == 1) {
                    if (row < 3) {
                        board[row][col] = new Piece(false); // Nero
                    } else if (row > 4) {
                        board[row][col] = new Piece(true);  // Bianco
                    }
                }
            }
        }
    }

    /**
     * Metodo di clonazione della scacchiera (utilizzato per favorire il funzionamento dell'IA).
     *
     * @return una nuova istanza di Board con lo stesso stato della scacchiera attuale.
     */
    @Override
    public Board clone() {
        Board newBoard = new Board();
        newBoard.board = new Piece[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != null) {
                    newBoard.board[i][j] = board[i][j].clone();
                }
            }
        }
        return newBoard;
    }

    /**
     * Restituisce il pezzo nella posizione specificata.
     *
     * @param row Riga della scacchiera.
     * @param col Colonna della scacchiera.
     * @return Il pezzo presente nella posizione specificata, o null se la posizione
     *         è fuori dai limiti o vuota.
     */
    public Piece getPiece(final int row, final int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            return null;
        }
        return board[row][col];
    }

    /**
     * Imposta un pezzo in una posizione specifica della scacchiera.
     *
     * @param row   Riga della scacchiera.
     * @param col   Colonna della scacchiera.
     * @param piece Il pezzo da posizionare.
     */
    public void setPiece(final int row, final int col, final Piece piece) {
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
            board[row][col] = piece;
        }
    }

    /**
     * Restituisce la scacchiera attuale.
     *
     * @return Matrice di pezzi che rappresenta la scacchiera.
     */
    public Piece[][] getBoard() {
        return board;
    }
}