package de.thb.tictactoe_server.tttsockets.commandHandlers;

import org.json.simple.JSONObject;

/**
 * CmdHandlers extract the given input from a Client-Websocket-Message regarding their topic
 */
public interface CmdHandler {
    public String handle(JSONObject payload);
}
