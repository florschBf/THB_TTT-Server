package de.thb.tictactoe_server.tttsockets.messageHandlers;

import org.json.simple.JSONObject;

import java.util.Objects;

/**
 * Klasse zum Auslesen der Spielzüge aus den Nachrichten, die mit "topic":"signup" gekennzeichnet sind.
 */
public class SignUpMsgHandler implements MsgHandler {

    /**
     * Method to handle messages received regarding the topic 'signup'
     * @param payload the message already parsed as JSONObject
     * @return String that transports needed actions to respond to the message
     */
    public String handle(JSONObject payload){
        if (!Objects.equals(payload.get("topic").toString(), "signup")){
            System.out.println("shouldnt be here");
            return "Error";
        }
        else {
            System.out.println("decoding signup command");
            String cmd = payload.get("command").toString();
            switch (cmd){
                case "register":
                    if (payload.get("player") != null){
                        return "add player called";
                    }
                    else { return "Error, not a valid register command"; }
                case "get":
                    if (Objects.equals(payload.get("players").toString(), "all")){
                        return "playerList called";
                    }
                    else { return "Error, not a valid get command"; }
                case "logoff":
                    if (Objects.equals(payload.get("player").toString(), "this")){
                        return "delete player called";
                    }
                default:
                    return "Error, not a valid signup command";
            }
        }
    }
}
