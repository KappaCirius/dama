    package it.unimol.dama;

    import javafx.application.Application;

    /**
     * Classe che funziona da entry point dell’applicazione.
     * Contiene il metodo main() che avvia l’applicazione JavaFX.
     */
    public final class Game {
        /**
         * Costruttore privato per impedire l'istanziazione.
         */
        private Game() { }
        /**
         * Metodo principale che avvia l'applicazione grafica.
         *
         * @param args Argomenti da riga di comando.
         */
        public static void main(final String[] args) {
            Application.launch(Graphic.class);
        }
    }
