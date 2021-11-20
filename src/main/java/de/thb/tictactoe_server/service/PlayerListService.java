package de.thb.tictactoe_server.service;

import de.thb.tictactoe_server.gameobject.GameObject;
import de.thb.tictactoe_server.gameobject.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Service that provides a List of active TicTacToe players in online mode
 */

@Service
public class PlayerListService {
    private ArrayList<Player> playerList;
    Player testPlayer = new Player("123", "example");

    public PlayerListService(){
        this.playerList = new ArrayList<Player>();
        this.addPlayerToList(testPlayer);
    }

    public ArrayList<Player> getPlayerList(){
        return playerList;
    }

    public void addPlayerToList(Player player){
        this.playerList.add(player);
        Long newUid = this.playerList.stream().count();
        player.setUid(newUid);
    }
}
