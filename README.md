# Dama - Gioco di Dama in Java

Questo progetto implementa un gioco di dama utilizzando Java, con un'interfaccia grafica realizzata con JavaFX.
Il gioco supporta sia il controllo da parte di un giocatore umano che da parte dell'intelligenza artificiale (IA) utilizzando l'algoritmo MinMax.

## Descrizione

Il gioco di dama consente di giocare contro un'intelligenza artificiale.
L'IA utilizza l'algoritmo MinMax con Alpha/Beta Pruning per prendere le decisioni migliori durante la partita.
La scacchiera è composta da 8x8 caselle e il gioco segue circa le regole tradizionali della dama, inclusa la promozione delle pedine.

### Funzionalità principali

- **Giocatore umano vs. IA**: l'IA prende decisioni tramite l'algoritmo MinMax, che valuta le mosse future.
- **Cattura delle pedine**: le pedine devono catturare quelle avversarie saltandole.
- **Promozione a regina**: una pedina raggiungendo la parte opposta della scacchiera viene promossa a dama.
- **Interfaccia grafica**: semplice interfaccia grafica in JavaFX per interagire con il gioco.

## Struttura del Progetto

Il progetto è suddiviso in diverse classi principali:

- **Board**: gestisce la scacchiera e la posizione delle pedine.
- **Piece**: rappresenta le pedine (bianche e nere) e le loro caratteristiche (normali o dame).
- **Move**: gestisce le mosse e le catture delle pedine.
- **Controller**: gestisce la logica di gioco, il turno del giocatore e le mosse.
- **MinMax**: implementa l'algoritmo MinMax per l'intelligenza artificiale.
- **Graphic**: gestisce l'interfaccia grafica del gioco utilizzando JavaFX.

## Requisiti

- Java 8 o superiore
- JavaFX (incluso nel JDK o configurato separatamente)
- Maven (per la gestione delle dipendenze)

## Come Eseguire il Progetto
Bisogna eseguire il tutto dalla classe Game.java

## Crediti
Il progetto è stato creato da:
- Cerio Kevin
- Martino Andrea
- Santopolo Andrea