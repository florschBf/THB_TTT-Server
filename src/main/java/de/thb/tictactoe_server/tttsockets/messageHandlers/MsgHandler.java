package de.thb.tictactoe_server.tttsockets.messageHandlers;

import org.json.simple.JSONObject;

/**
 * CmdHandlers extract the given input from a Client-Websocket-Message regarding their topic
 */
public interface MsgHandler {
    public String handle(JSONObject payload);
}
