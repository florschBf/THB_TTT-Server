package de.thb.tictactoe_server.tttsockets;

import de.thb.tictactoe_server.gameobject.Player;
import org.java_websocket.WebSocket;

import java.util.Random;


public class GameSessionHandler {
    private Player player1, player2;
    private Integer[] gameboard = {0,0,0,0,0,0,0,0,0};
    private WebSocket p1, p2;
    private boolean player1Turn;
    private Integer gameid = null;

    /**
     * Constructor benötigt zwei Player Objekte zum Spielen
     * @param player1 Spieler 1 mit websocket/uid/etc
     * @param player2 Spieler 2
     */
    public GameSessionHandler(Player player1, Player player2) {
        this.player1 = player1;
        System.out.println(this.player1);
        this.player2 = player2;
        System.out.println(this.player2);
        if (player1 == player2){
            //TODO catch this case and tell client to fuck off
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
        this.gameid = player1.getUid() * player2.getUid();
        System.out.println("This is the gameID: " + this.gameid);
        this.player1.setGameSession(this);
        this.player2.setGameSession(this);

        //sending challenge to player2, telling p1 to wait
        this.p1.send("{\"topic\":\"gameSession\",\"startgame\":\"hold\"}");
        this.p2.send("{\"topic\":\"gameSession\",\"startgame\":\"challenged\"}");
    }

    /**
     * Methode zur Initialisierung des Spiels nach confirm
     */
    public Boolean initGame(String message){
        if (message.equals("gameConfirmed")){
            //START GAME
            this.p1.send("{\"topic\":\"gameSession\",\"startgame\":\"confirm\",\"opponent\":\""+this.player2.getName()+"\"}");
            this.p2.send("{\"topic\":\"gameSession\",\"startgame\":\"confirm\",\"opponent\":\""+this.player1.getName()+"\"}");
            //Tell Clients who goes first
            if(player1Turn) {
                this.p1.send("{\"topic\":\"gameSession\",\"gameState\":\"yourTurn\"}");
                this.p2.send("{\"topic\":\"gameSession\",\"gameState\":\"opponentsTurn\"}");
            }
            else {
                this.p1.send("{\"topic\":\"gameSession\",\"gameState\":\"opponentsTurn\"}");
                this.p2.send("{\"topic\":\"gameSession\",\"gameState\":\"yourTurn\"}");
            }
            return true;
        }
        else if (message.equals("gameDenied")){
            //TODO CLEAN UP DENIED GAME SESSION
            this.p1.send("{\"topic\":\"gameSession\",\"startgame\":\"denied\"}");
            this.p2.send("{\"topic\":\"gameSession\",\"startgame\":\"denied\"}");
            return false;
        }
        return false;

    }

    /**
     * Methode zur Markierung der TicTacToe-Felder
     * Speichert Markierung für Spieler im GameBoard
     * Gibt Bestätigung zurück
     * Lehnt ab, wenn Spieler nicht am Zug
     * @param conn Verbindung des eingebenden Spielers
     * @param feld gewähltes Feld von links oben 1 bis rechts unten 9
     */
    public void move(WebSocket conn, Integer feld) {
        //Find out who sent the move and whose turn it is
        Boolean p1 = false;
        if (player1.getConn().equals(conn)){
            p1 = true;
        }
        if (player1Turn && p1){
            if (gameboard[feld-1] == 0){
                gameboard[feld-1] = 1;
                this.player1Turn = false;
                conn.send("{\"topic\":\"gameMove\",\"marked\":\""+feld+"\",\"whoseTurn\":\"opponentsTurn\" }");
                this.player2.getConn().send("{\"topic\":\"gameMove\",\"marked\":\""+feld+"\",\"whoseTurn\":\"yourTurn\" }");
                if(checkGameOver(gameboard)){
                    //TODO game is over, tell clients
                }
                else{
                    //Spiel geht weiter
                };
            }
            else{
                conn.send("{\"topic\":\"gameMove\",\"whoseTurn\":\"opponentsTurn\"}");
            }
        }
        else if (!player1Turn && !p1){
            if (gameboard[feld-1] == 0){
                gameboard[feld-1] = 2;
                this.player1Turn = true;
                conn.send("{\"topic\":\"gameMove\",\"marked\":\""+feld+"\",\"Player\":\"Player2Icon\" }");
                this.player1.getConn().send("{ \"topic\":\"gameMove\",\"Marked\":\""+feld+"\",\"Player\":\"Player2Icon\" }");
                if(checkGameOver(gameboard)){
                    //TODO game is over, tell clients
                }
                else{
                    //Spiel geht weiter
                };
            }
            else{
                conn.send("position already taken, bugger off");
            }
        }
        else{
            conn.send("Not your turn");
        }
    }

    private boolean checkGameOver(Integer[] gameboard){
        //TODO Spielfeld auf Gewinner prüfen --> Einzelspieler-Logik nutzen/nutzbar machen
        return false;
    };

    public Player getPlayer1(){
        return player1;
    }
    public Player getPlayer2(){return player2;}
}
