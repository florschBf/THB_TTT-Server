package de.thb.tictactoe_server.tttsockets;

import de.thb.tictactoe_server.gameobject.Player;
import org.java_websocket.WebSocket;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Objects;


public class SocketLogOnHandler {
    private final ArrayList<Player> playerList = new ArrayList<>();

    public SocketLogOnHandler(){

    }

    public void addConnToPlayerList(Player newPlayer){
        this.playerList.add(newPlayer);
        System.out.println("added " + newPlayer + " to PlayerList");
        System.out.println(this.playerList);
    }

    public ArrayList<Player> getPlayerList(){
        return this.playerList;
    }

    /**
     * Methode um Spieler aus der Liste zu entfernen
     * @param conn das Websocket Objekt, das zum Player gehört, der entfernt wird
     * @return boolean true, wenn alles geklappt hat, false bei exception (duh)
     */
    public boolean removePlayer(WebSocket conn){
        //TODO player deletion is wonky... delete on list iteration not working! Solved by closing Conn and filtering for closed -> NOT solved, TEST
        //currently does not delete on socket but removes closed connections from the list -> still needs fixing? Is workable though, updates the list properly
        //conn.close() not working
        try{
            System.out.println(conn);
            Player player = getPlayerByConn(conn);
            System.out.println("Player found with Socket: " + player.getConn() + "\n That's him: " + player);
            this.playerList.remove(player);
            conn.close();
            System.out.println("removed " + player + " from PlayerList");
            return true;
        }
        catch (Exception e){
            System.out.println("no player with that socket?");
            System.out.println(this.playerList);
            return false;
        }

    }

    /**
     * Method to return the playerList in JSON format for broadcast to all clients
     * @return String der Spielerliste mit "topic":"signup", "players":"all" und Spielerobjekten, TTT-Protokoll V1.1
     */
    public String returnPlayers(){
        //seems fine new
        JSONObject list = new JSONObject();
        list.put("topic","signup");
        list.put("players","all");
        Integer i = 0;
        for (Player player : playerList){
            if(player.getConn().isOpen()){
                JSONObject info = new JSONObject();
                info.put("name", player.getName());
                info.put("playerUID", player.getUid());
                list.put(i,info);
                i++;
            }
        }
        System.out.println(list);
        String payload = list.toJSONString();
        System.out.println(payload);
        return payload;
    }


    /**
     * Methode um Player-Objekt anhand des Websockets zu finden
     * @param conn das Websocket Objekt, das zum Player gehört
     * @return Player der gesuchte Spieler
     */
    public Player getPlayerByConn(WebSocket conn){
        System.out.println("Looking for this connection " + conn);
        System.out.println(this.returnPlayers());
        Player gesuchter = null;
        for (Player player : this.playerList) {
            System.out.println("Finding in List: " + player.getConn());
            if (Objects.equals(player.getConn(), conn)){
                System.out.println(this.playerList.indexOf(player));
                gesuchter = player;
                System.out.println("gesuchter: " + gesuchter);
            }
        }
        return gesuchter;
    }

    //TODO think this is broken -> not in use though, need to try
    //aktuell meist getPlayerByUid -> player.getConn() als Umweg - ist im Endeffekt, was hier auch gemacht wird
    //tendenziell einfach löschen
    public WebSocket getConnByUid(Integer uid){
        System.out.println("Looking for this guy " + uid);
        System.out.println(this.returnPlayers());
        Player player = this.playerList.stream().findAny().filter(p -> (Objects.equals(p.getUid(), uid))).orElseThrow();
        return player.getConn();
    }

    /**
     * Methode um ein Player-Objekt anhand der UID zu finden
     * @param uid des gewünschten Player-Objekts
     * @return Player aus der playerList
     */
    public Player getPlayerByUid(Integer uid){
        System.out.println("Looking for this guy " + uid);
        System.out.println(this.returnPlayers());
        Player gesuchter = null;
        for (Player player : this.playerList) {
            System.out.println("Finding in List: " + player.getUid());
            if (Objects.equals(player.getUid(), uid)){
                System.out.println(this.playerList.indexOf(player));
                gesuchter = player;
                System.out.println("gesuchter: " + gesuchter);
            }
        }
        return gesuchter;
    }

    /**
     * Methode um Player als beschäftigt zu markieren um weitere Spieleanfragen zu blocken
     * @param player der gewünschte Spieler aus der Liste
     * @return String cmd zum Broadcast an alle in der Spielerliste, damit der Spieler dort auch als beschäftigt markiert werden kann.
     */
    public String setPlayerAsBusy(Player player){
        String playerID = player.getUid().toString();
        System.out.println("Setzte Player " + playerID + " als beschäftigt.");
        String cmd = "{\"topic\":\"signup\",\"players\":\""+playerID+" is busy\"}";
        return cmd;
    }

    /**
     * Methode um Spieler in der Liste wieder zum Spielen freizugeben
     * @param player der gewünschte Spieler aus der Liste
     * @return String cmd zum Broadcast an alle in der Spielerliste, damit der Spieler dort auch wieder als frei markiert werden kann.
     */
    public String setPlayerAsFree(Player player){
        String playerID = player.getUid().toString();
        System.out.println("Setzte Player " + playerID + " als frei.");
        String cmd = "{\"topic\":\"signup\",\"players\":\""+playerID+" is available\"}";
        return cmd;
    }
}
