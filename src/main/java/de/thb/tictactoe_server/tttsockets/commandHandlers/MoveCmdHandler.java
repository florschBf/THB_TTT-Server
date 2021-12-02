package de.thb.tictactoe_server.tttsockets.commandHandlers;

import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.util.Objects;

public class MoveCmdHandler implements CmdHandler{
    @Override
    public String handle(JSONObject payload) {
        if (payload.get("topic").toString() != "gameMove"){
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
                            return "Feld " + field + " gesetzt";
                        }
                        else { return "not a proper field request";}
                    } catch (Exception e){
                        return "Error, not a proper mark request";
                    }
            }

        }
        return null;
    }
}
