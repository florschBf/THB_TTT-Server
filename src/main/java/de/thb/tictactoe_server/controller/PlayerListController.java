package de.thb.tictactoe_server.controller;

import de.thb.tictactoe_server.gameobject.Player;
import de.thb.tictactoe_server.service.PlayerListService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Controller that adds REST Endpoints to access PlayerList
 */

@RestController
@RequestMapping("/playerList")
public class PlayerListController {
    private final PlayerListService playerListService;

    public PlayerListController(PlayerListService playerListService){
        this.playerListService = playerListService;
    }

    @ResponseBody
    @PostMapping("/addPlayer")
    public Player addPlayer(@RequestBody Player player){

        return this.playerListService.addPlayerToList(player);
        }

    @GetMapping("showMe")
    private ArrayList<Player> show(){ return playerListService.getPlayerList();}

    @GetMapping("/list")
    public final PlayerListService getPlayerListService(){
        return playerListService;
    }
}
