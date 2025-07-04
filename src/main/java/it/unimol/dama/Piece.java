package it.unimol.dama;

/**
 * Classe che rappresenta una singola pedina del gioco.
 * Gestisce le proprietà fondamentali della pedina, come il colore e lo stato "king" (dama).
 */
public class Piece {
    private final boolean isWhite;
    private boolean isKing;

    /**
     * Costruttore della classe Piece.
     * @param isWhite Indica se il pezzo è bianco.
     */
    public Piece(final boolean isWhite) {
        this.isWhite = isWhite;
        this.isKing = false;
    }

    /**
     * Restituisce true se il pezzo è bianco.
     * @return true se il pezzo è bianco, false altrimenti.
     */
    public boolean isWhite() {
        return isWhite;
    }

    /**
     * Restituisce true se il pezzo è re.
     * @return true se il pezzo è re, false altrimenti.
     */
    public boolean isKing() {
        return isKing;
    }

    /**
     * Promuove il pezzo a re.
     */
    public void crown() {
        isKing = true;
    }

    /**
     * Clona il pezzo corrente.
     * @return Un nuovo oggetto Piece con gli stessi attributi.
     */
    public Piece clone() {
        Piece newPiece = new Piece(this.isWhite);
        newPiece.isKing = this.isKing;
        return newPiece;
    }
}
