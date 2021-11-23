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

    public void addPlayerToList(Player player){
        this.playerList.add(player);
        Long newUid = (long) this.playerList.size();
        player.setUid(newUid);
    }
}
