package de.thb.tictactoe_server.tttsockets;

import de.thb.tictactoe_server.gameobject.Player;
import org.java_websocket.WebSocket;
import org.json.simple.JSONArray;
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

    public void removePlayer(WebSocket conn){
        //TODO player deletion is wonky... delete on iteration! Solved by closing Conn and filtering for closed -> should be solved, TEST
        try{
            System.out.println(conn);
            Player player = this.playerList.stream().findAny().filter(player1 -> (!player1.getConn().isOpen())).orElseThrow();
            System.out.println("Player found with Socket: " + player.getConn() + "\n That's him: " + player);
            this.playerList.remove(player);
            conn.close();
            System.out.println("removed " + player + " from PlayerList");
            Player cleanUp = this.playerList.stream().findAny().filter(player1 -> player1.getConn().isClosed()).orElseThrow();
            this.playerList.remove(cleanUp);
        }
        catch (Exception e){
            System.out.println("no player with that socket?");
        }
        System.out.println(this.playerList);
    }

    //TODO seems malformed - rework
    public String returnPlayers(){
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
    public WebSocket getConnByUid(Integer uid){
        System.out.println("Looking for this guy " + uid);
        System.out.println(this.returnPlayers());
        Player player = this.playerList.stream().findAny().filter(p -> (Objects.equals(p.getUid(), uid))).orElseThrow();
        return player.getConn();
    }

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
}
