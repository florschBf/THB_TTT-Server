package de.thb.tictactoe_server.tttsockets;

import de.thb.tictactoe_server.TicTacToeSocketServer;
import de.thb.tictactoe_server.gameobject.Player;
import org.java_websocket.WebSocket;

import java.util.Arrays;
import java.util.Random;

/**
 * Klasse um TicTacToe-Spiele zwischen zwei Spielern zu verwalten
 */
public class GameSessionHandler {
    private Player player1, player2;
    private Integer[] gameboard = {0,0,0,0,0,0,0,0,0};
    private WebSocket p1, p2;
    private TicTacToeSocketServer server;
    private boolean player1Turn, p1ready = false, p2ready = false;
    private boolean draw = false;
    private Integer gameid = null;
    private int p1Icon, p2Icon;

    /**
     * Constructor benötigt zwei Player Objekte zum Spielen
     * @param player1 Spieler 1 mit websocket/uid/etc
     * @param player2 Spieler 2
     */
    public GameSessionHandler(Player player1, Player player2, TicTacToeSocketServer server) {
        this.player1 = player1;
        this.player2 = player2;
        this.server = server;
        if (player1 == player2){
            //prevented on client side, client can't see and select itself in list, not preventing it, just logging
            System.out.println("well, that's gonna be boring");
        }
        //Markiere Spieler als in einer Gamesession, damit keine weiteren geöffnet werden können
        this.player1.setInGame(true);
        this.player2.setInGame(true);
        this.p1 = this.player1.getConn();
        this.p2 = this.player2.getConn();
        Random rd = new Random();
        this.player1Turn = rd.nextBoolean();
        System.out.println("Is it player1 turn?: " + this.player1Turn);
        this.gameid = player1.getUid() + player2.getUid(); //adding instead of multiplication
        System.out.println("This is the gameID: " + this.gameid);
        this.player1.setGameSession(this);
        this.player2.setGameSession(this);
    }

    /**
     * Methode zur Initialisierung des Spiels nach confirm oder Abbruch nach deny
     * @param message Ausgewertete Antwort des angefragten Spielers
     * @return true = Spiel zustande gekommen, warte auf Zug | false = Spiel abgelehnt, Spielsession rückabwickeln
     */
    public void initGame(String message){
        if (message.equals("gameConfirmed")){
            //START GAME
            this.p1.send("{\"topic\":\"gameSession\"," +
                    "\"command\":\"startgame\"," +
                    "\"state\":\"confirmed\"," +
                    "\"opponent\":\""+this.player2.getName()+"\"," +
                    "\"opponentIcon\":\""+this.player2.getIcon()+"\"}");
            this.p2.send("{\"topic\":\"gameSession\"," +
                    "\"command\":\"startgame\"," +
                    "\"state\":\"confirmed\"," +
                    "\"opponent\":\""+this.player1.getName()+"\"," +
                    "\"opponentIcon\":\""+this.player1.getIcon()+"\"}");
            //Warte auf Spieler-Confirm
        }
        else if (message.equals("gameDenied")){
            //Think that is all that needs cleaning
            this.p1.send("{\"topic\":\"gameSession\",\"command\":\"startgame\",\"state\":\"denied\"}");
            this.p2.send("{\"topic\":\"gameSession\",\"command\":\"startgame\",\"state\":\"denied\"}");
            endGameSession();
        }
    }

    /**
     * Methode um beiden Clients zu sagen, wer anfängt bzw. wer am Zug ist
     */
    public void tellTurns(){
        if (p1ready && p2ready){
            //Tell Clients who goes first as both are ready
            if(player1Turn) {
                this.p1.send("{\"topic\":\"gameSession\",\"command\":\"gameState\",\"info\":\"yourTurn\"}");
                this.p2.send("{\"topic\":\"gameSession\",\"command\":\"gameState\",\"info\":\"opponentsTurn\"}");
            }
            else {
                this.p1.send("{\"topic\":\"gameSession\",\"command\":\"gameState\",\"info\":\"opponentsTurn\"}");
                this.p2.send("{\"topic\":\"gameSession\",\"command\":\"gameState\",\"info\":\"yourTurn\"}");
            }
        }
        else {
            //waiting for both players to be ready
            System.out.println("both players not ready yet");
        }
    }

    /**
     * Methode zur Markierung der TicTacToe-Felder
     * Speichert Markierung für Spieler im GameBoard.
     * Gibt Bestätigung zurück.
     * Lehnt ab, wenn Spieler nicht am Zug ist.
     * @param conn Verbindung des eingebenden Spielers
     * @param feld gewähltes Feld von links oben 1 bis rechts unten 9
     */
    public void move(WebSocket conn, int feld) {
        System.out.println("gameSession move called");
        //Find out who sent the move
        boolean isSenderP1 = this.p1.equals(conn);

        //If sender and player1Turn align, mark gameboard, confirm move
        //else deny move, repeat opponentsTurn
        if (player1Turn && isSenderP1){
            if (gameboard[feld] == 0){
                gameboard[feld] = 1;
                this.player1Turn = false;
                System.out.println("sending move to players here now");
                conn.send("{\"topic\":\"gameMove\",\"command\":\"mark\",\"marked\":\""+feld+"\",\"player\":\"you\"}");
                this.p2.send("{\"topic\":\"gameMove\",\"command\":\"mark\",\"marked\":\""+feld+"\",\"player\":\"opponent\"}");
                checkGameOver(gameboard);
            }
            else{
                //Feld schon gesetzt, geht nicht //
                //Wird eigentlich am Client bereits verhindert
                System.out.println("invalid move");
                conn.send("{\"topic\":\"gameMove\",\"command\":\"mark\",\"error\":\"fieldTaken\",\"boardState\":\""+gameboard.toString()+"\"}");
            }
        }
        else if (!player1Turn && !isSenderP1){
            if (gameboard[feld] == 0){
                gameboard[feld] = 2;
                this.player1Turn = true;
                System.out.println("sending move to players here now");
                conn.send("{\"topic\":\"gameMove\",\"command\":\"mark\",\"marked\":\""+feld+"\",\"player\":\"you\" }");
                this.p1.send("{ \"topic\":\"gameMove\",\"command\":\"mark\",\"marked\":\""+feld+"\",\"player\":\"opponent\"}");
                checkGameOver(gameboard);
            }
            else{
                //Feld schon gesetzt, geht nicht //
                //Wird eigentlich am Client bereits verhindert
                System.out.println("invalid move");
                conn.send("{\"topic\":\"gameMove\",\"command\":\"mark\",\"error\":\"fieldTaken\",\"boardState\":\""+gameboard.toString()+"\"}");
            }
        }
        else{
            //Client should not be sending, tell them
            System.out.println("Not setting the move");
            conn.send("{\"topic\":\"gameSession\",\"command\":\"gameState\",\"info\":\"opponentsTurn\"}");
        }
    }

    /**
     * Methode um zu prüfen, ob einer der Spieler gewonnen hat.
     * Falls es einen Sieger oder ein Unentschieden gibt, werden automatisch die Spieler benachrichtigt und die Session beendet
     * @param gameboard Integer-Array, welches das Spielbrett auf den Positionen 0-8 repräsentiert.
     *                  0 = leer
     *                  1 = Zeichen von Spieler 1
     *                  2 = Zeichen von Spieler 2
     */
    private void checkGameOver(Integer[] gameboard){
        boolean someoneWon = false;
        //Zeilen prüfen
        if ((gameboard[0] == 1 && gameboard[1] == 1 && gameboard[2] == 1) ||
                (gameboard[3] == 1 && gameboard[4] == 1 && gameboard[5] == 1) ||
                (gameboard[6] == 1 && gameboard[7] == 1 && gameboard[8] == 1)) {
            // 1 hat gewonnen
            someoneWon = true;
            System.out.println("P1 hat gewonnen, sage Bescheid und beende Session");
            this.p1.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"youwin\"}");
            this.p2.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"youlose\"}");
            endGameSession();
        }
        if ((gameboard[0] == 2 && gameboard[1] == 2 && gameboard[2] == 2) ||
                (gameboard[3] == 2 && gameboard[4] == 2 && gameboard[5] == 2) ||
                (gameboard[6] == 2 && gameboard[7] == 2 && gameboard[8] == 2)) {
            //2 won!
            someoneWon = true;
            System.out.println("P2 hat gewonnen, sage Bescheid und beende Session");
            this.p1.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"youlose\"}");
            this.p2.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"youwin\"}");
            endGameSession();
        }

        // Spalten prüfen
        if ((gameboard[0] == 1 && gameboard[3] == 1 && gameboard[6] == 1) ||
                (gameboard[1] == 1 && gameboard[4] == 1 && gameboard[7] == 1) ||
                (gameboard[2] == 1 && gameboard[5] == 1 && gameboard[8] == 1)) {
            // 1 won!
            someoneWon = true;
            System.out.println("P1 hat gewonnen, sage Bescheid und beende Session");
            this.p1.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"youwin\"}");
            this.p2.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"youlose\"}");
            endGameSession();
        }
        if ((gameboard[0] == 2 && gameboard[3] == 2 && gameboard[6] == 2) ||
                (gameboard[1] == 2 && gameboard[4] == 2 && gameboard[7] == 2) ||
                (gameboard[2] == 2 && gameboard[5] == 2 && gameboard[8] == 2)) {
            //2 won!
            someoneWon = true;
            System.out.println("P2 hat gewonnen, sage Bescheid und beende Session");
            this.p1.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"youlose\"}");
            this.p2.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"youwin\"}");
            endGameSession();
        }

        // Diagonale prüfen
        if ((gameboard[0] == 1 && gameboard[4] == 1 && gameboard[8] == 1) ||
                (gameboard[2] == 1 && gameboard[4] == 1 && gameboard[6] == 1)) {
            //1 won!
            someoneWon = true;
            System.out.println("P1 hat gewonnen, sage Bescheid und beende Session");
            this.p1.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"youwin\"}");
            this.p2.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"youlose\"}");
            endGameSession();
        }
        if ((gameboard[0] == 2 && gameboard[4] == 2 && gameboard[8] == 2) ||
                (gameboard[2] == 2 && gameboard[4] == 2 && gameboard[6] == 2)) {
            //2 won!
            someoneWon = true;
            System.out.println("P2 hat gewonnen, sage Bescheid und beende Session");
            this.p1.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"youlose\"}");
            this.p2.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"youwin\"}");
            endGameSession();
        }

        if (someoneWon == false){
            // check for draw
            this.draw = true;
            for (int i = 0; i < Arrays.stream(gameboard).count(); i++) {
                if (gameboard[i] == 0) {
                    //still empty space on the board, not a draw yet
                    this.draw = false;
                }
            }
            if (draw){
                System.out.println("Unentschieden, sage Bescheid und beende Session");
                this.p1.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"draw\"}");
                this.p2.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"draw\"}");
                endGameSession();
            }
        }
        else { System.out.println("don't need to check for draw, have a winner already, doesnt matter if board is full"); }
    }

    public Player getPlayer1(){
        return player1;
    }
    public Player getPlayer2(){return player2;}

    /**
     * Methode um mitzuteilen, dass das Spiel beendet wurde,
     * @param player Der Spieler, dessen Verbindung abgebrochen hat. Der braucht keine Nachricht mehr :-)
     */
    public void quitGameDisconnect(Player player) {
        System.out.println("informing remaining player");
        if (player == player1){
            p2.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"now\",\"reason\":\"opponentDisco\"}");
        }
        else { p1.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"now\",\"reason\":\"opponentDisco\"}"); }
        System.out.println("closing game session");
        endGameSession();
    }

    /**
     * Methode um die Spieler wieder freizuschalten, damit sie ein neues Spiel auf dem Server starten können.
     */
    public void endGameSession(){
        //Extra vorsichtig mit den try/catch, falls der Spieler schon gelöscht wurde. Sollte eigentlich nicht vorkommen, da Playerobjekte erst von der garbage collection erwischt werden sollten
        try {
            this.player1.setInGame(false);
            this.player1.setGameSession(null);
        }
        catch (Exception e){
            System.out.println("Guess player wasn't there anymore..");
            e.printStackTrace();
        }
        try {
            this.player2.setInGame(false);
            this.player2.setGameSession(null);
        }
        catch (Exception e){
            System.out.println("Guess player wasn't there anymore..");
            e.printStackTrace();
        }
        this.server.unbusyPlayers(this.player1, this.player2);
    }

    /**
     * Methode registriert, dass die Spieler bereit sind. Wird vom Server auf Nachricht des Clients aufgerufen.
     * @param player Der Spieler, der bereit ist.
     */
    public void setPlayerReady(Player player) {
        if (player == this.player1){
            this.p1ready = true;
            System.out.println("setting p1 ready: "+ this.p1ready);
            System.out.println("p2 status: " + this.p2ready);
        }
        else if (player == this.player2){
            this.p2ready = true;
            System.out.println("setting p2 ready: "+ this.p2ready);
            System.out.println("p1 status: " + this.p1ready);
        }
        else {
            System.out.println("I dont know that player, cant be ready... this is bad");
        }
    }
}
