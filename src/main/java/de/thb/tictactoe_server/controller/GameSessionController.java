package de.thb.tictactoe_server.controller;

import de.thb.tictactoe_server.service.GameSessionService;

public class GameSessionController {
    private GameSessionService gameSessionService;

    private GameSessionController(GameSessionService gameSessionService){
        this.gameSessionService = gameSessionService;
    }
}
