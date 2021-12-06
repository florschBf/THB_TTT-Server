package de.thb.tictactoe_server.tttsockets;

import de.thb.tictactoe_server.gameobject.Player;
import org.java_websocket.WebSocket;

import java.util.Random;


public class GameSessionHandler {
    private Player player1, player2;
    private Integer[] gameboard = {0,0,0,0,0,0,0,0,0};
    private boolean player1Turn;
    private Integer gameid = null;

    /**
     * Constructor benötigt zwei Player Objekte zum Spielen
     * @param player1 Spieler 1 mit websocket/uid/etc
     * @param player2 Spieler 2
     */
    public GameSessionHandler(Player player1, Player player2) {
        System.out.println(player1);
        System.out.println(player2);
        this.player1 = player1;
        System.out.println(this.player1);
        this.player2 = player2;
        System.out.println(this.player2);
        if (player1 == player2){
            //TODO catch this case and tell client to fuck off
            System.out.println("well, that's gonna be boring");
        }
        this.player1.setInGame(true);
        this.player2.setInGame(true);
        Random rd = new Random();
        this.player1Turn = rd.nextBoolean();
        System.out.println(this.player1Turn);
        this.gameid = player1.getUid() * player2.getUid();
        System.out.println(this.gameid);
        this.player1.setGameSession(this);
        this.player2.setGameSession(this);
        this.player1.getConn().send("{\"opponent\":\""+this.player2.getName()+"\"}");
        this.player2.getConn().send("{\"opponent\":\""+this.player1.getName()+"\"}");
    }

    /**
     * Methode zur Initialisierung des Spiels
     */
    public void initGame(){

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
                this.player1.getConn().send("{ \"Marked\":\""+feld+"\",\"Player\":\"Player2Icon\" }");
            }
            else{
                conn.send("position already taken, bugger off");
            }
        }
        else{
            conn.send("Not your turn");
        }
    }
}
