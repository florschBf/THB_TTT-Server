package de.thb.tictactoe_server.gameobject;

/**
 * Baseline interface for all game related objects
 * -> enforces a method to get a name and a UID from any object
 * Only used for Player in TicTacToe so far
 */
public interface GameObject {
    String getName();
    Integer getUid();
}
