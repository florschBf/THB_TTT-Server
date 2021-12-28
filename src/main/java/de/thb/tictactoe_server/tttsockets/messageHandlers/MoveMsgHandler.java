package de.thb.tictactoe_server.tttsockets.messageHandlers;

import org.json.simple.JSONObject;

import java.util.Objects;

/**
 * Klasse zum Auslesen der Spielzüge aus den Nachrichten, die mit "topic":"gameMove" gekennzeichnet sind.
 */
public class MoveMsgHandler implements MsgHandler {
    @Override
    public String handle(JSONObject payload) {
        if (!Objects.equals(payload.get("topic").toString(), "gameMove")){
            System.out.println("shouldnt be here");
            return "Error";
        }
        else {
            String command = payload.get("command").toString();
            switch (command){
                case "mark":
                    try {
                        String field = payload.get("field").toString();
                        if (Objects.equals(field, "0") || Objects.equals(field, "1") || Objects.equals(field, "2") || Objects.equals(field, "3") || Objects.equals(field, "4")
                                || Objects.equals(field, "5") || Objects.equals(field, "6") || Objects.equals(field, "7") || Objects.equals(field, "8")){
                            System.out.println("Proper field found");
                            String result = "Feld " + field + " gesetzt";
                            System.out.println(result);
                            return result;
                        }
                        else { return "Error, that field does not exist on our gameboard";}
                    } catch (Exception e){
                        return "Error, not a proper mark request";
                    }
            }

        }
        return null;
    }
}
