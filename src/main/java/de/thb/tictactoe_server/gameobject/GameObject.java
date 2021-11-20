package de.thb.tictactoe_server.gameobject;

/**
 * Baseline interface for all game related objects
 * -> enforces a method to get a name and a UID from any object
 */
public interface GameObject {
    public String getName();
    public Long getUid();
}
