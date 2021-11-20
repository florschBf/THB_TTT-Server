package de.thb.tictactoe_server.controller;

import de.thb.tictactoe_server.gameobject.Player;
import de.thb.tictactoe_server.service.PlayerListService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

/**
 * Controller that adds REST Endpoints to access PlayerList
 */

@RestController
@RequestMapping("/playerList")
public class PlayerListController {
    private PlayerListService playerListService;

    public PlayerListController(PlayerListService playerListService){
        this.playerListService = playerListService;
    }

    @ResponseBody
    @PostMapping("/addPlayer")
    public String addPlayer(@RequestBody Player player){
        this.playerListService.addPlayerToList(player);
        return "User added, thank you.";
        }

    @GetMapping("showMe")
    private ArrayList<Player> show(){ return playerListService.getPlayerList();}
}
