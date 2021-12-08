package de.thb.tictactoe_server.tttsockets.messageHandlers;

import org.json.simple.JSONObject;

import java.util.Objects;

public class SignUpMsgHandler implements MsgHandler {

    public String handle(JSONObject payload){
        if (!Objects.equals(payload.get("topic").toString(), "signup")){
            System.out.println("shouldnt be here");
            return "Error";
        }
        else {
            System.out.println("decoding signUp command");
            try {
                if (payload.get("register").toString().equals("player")){
                    return "add player called";
                }
                else if (payload.get("register").toString().equals("logoff")){
                    return "delete player called";
                }
                else { return "no valid register command found";}
            } catch (Exception e){
                //Wasn't register, must be a get for the playerList
                if (payload.get("get").toString().equals("players")){
                    return "playerList called";
                }
                else { return "no valid get command found";}
            }

        }
    }
}
