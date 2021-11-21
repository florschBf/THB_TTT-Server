package de.thb.tictactoe_server.controller;

import de.thb.tictactoe_server.gameobject.Player;
import de.thb.tictactoe_server.service.PlayerListService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

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
    //former "showMe"
    @GetMapping("/")
    private ArrayList<Player> show(){ return playerListService.getPlayerList();}


}
