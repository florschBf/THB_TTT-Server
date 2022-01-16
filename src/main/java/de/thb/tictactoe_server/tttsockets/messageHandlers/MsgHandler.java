package de.thb.tictactoe_server.tttsockets.messageHandlers;

import org.json.simple.JSONObject;

/**
 * MsgHandlers extract the given input from a Client-Websocket-Message regarding their topic.
 * Interface to be implemented by different kinds of MsgHandlers depending on what topic the message is about.
 */
public interface MsgHandler {
    public String handle(JSONObject payload);
}
