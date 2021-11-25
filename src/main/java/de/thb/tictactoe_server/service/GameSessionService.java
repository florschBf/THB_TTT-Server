package de.thb.tictactoe_server.service;

import de.thb.tictactoe_server.gameobject.Player;

import java.lang.reflect.Array;

/**
 * Service that handles TicTacToe Game Sessions for THB_TicTacToe Android App
 *
 */
public class GameSessionService {
    private Player player1, player2;
    private Array gameBoard;
    private Integer whosTurnIsIt;
}
