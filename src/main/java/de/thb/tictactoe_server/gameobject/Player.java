package de.thb.tictactoe_server.gameobject;

import de.thb.tictactoe_server.tttsockets.GameSessionHandler;
import org.java_websocket.WebSocket;

/**
 * Player GameObject holds Info about participating clients
 * Includes name, firebaseId, uid, socketConnection, gameSession, icon_choice if applicable
 * implements GameObject interface
 */
public class Player implements GameObject{
    private String name;
    private String firebaseId;
    private Integer uid;
    private String icon;
    private WebSocket conn = null;
    private boolean inGame = false;
    private boolean busy = false;
    private GameSessionHandler gameSession;

    public Player(){
    }

    public Player(String firebaseId, String name) {
        this.name = name;
        this.firebaseId = firebaseId;
    }

    public Player(String firebaseId, String name, WebSocket conn) {
        this.name = name;
        this.firebaseId = firebaseId;
        this.conn = conn;
    }

    // Getters und Setters
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public void setGameSession(GameSessionHandler session){
        this.gameSession = session;
    }
    public GameSessionHandler getGameSession(){
        System.out.println("Finding gameSession: " + this.gameSession);
        return this.gameSession;
    }
    public String getName(){
        return name;
    }
    public Integer getUid(){
        return uid;
    }
    public String getFirebaseId() {
        return firebaseId;
    }
    public void setUid (){
        this.uid = this.getConn().hashCode();
    }
    public void setUid(Integer uid){
        this.uid = uid;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setFirebaseId(String firebaseId){
        this.firebaseId = firebaseId;
    }
    public WebSocket getConn() {
        return conn;
    }
    public void setConn(WebSocket conn) {
        this.conn = conn;
    }
    public void setInGame(boolean statusUpdate){
        this.inGame = statusUpdate;
    }
    public boolean getInGame(){
        return this.inGame;
    }
    public String isBusy() {
        if (busy){
            return "busy";
        }
        else { return "free";}
    }
    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    /**
     * Methode zum Vergleich von Websocket-Connections
     * @param conn Zu vergleichende Websocket-Connection
     * @return boolean
     */
    public boolean equals(WebSocket conn){
        if(conn == this.getConn()){
            return true;
        }
        else{
            return false;
        }
    }
}
