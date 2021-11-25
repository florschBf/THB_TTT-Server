package de.thb.tictactoe_server.service;

import de.thb.tictactoe_server.gameobject.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Service that provides a List of active TicTacToe players in online mode
 */

@Service
public class PlayerListService {
    private final ArrayList<Player> playerList = new ArrayList<>();
    Player testPlayer = new Player("123", "example");

    public PlayerListService(){
        this.addPlayerToList(testPlayer);
    }

    public ArrayList<Player> getPlayerList(){
        return playerList;
    }

    public Player addPlayerToList(Player player){
        this.playerList.add(player);
        Integer newUid = this.playerList.size();
        if(player.getConn() != null){
            player.setUid();
        }
        else{
            player.setUid(newUid);
        }

        return player;
    }
}
