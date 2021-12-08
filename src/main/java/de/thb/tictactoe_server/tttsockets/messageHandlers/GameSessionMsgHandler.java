package de.thb.tictactoe_server.tttsockets.messageHandlers;

import org.json.simple.JSONObject;

import java.util.Objects;

public class GameSessionMsgHandler implements MsgHandler {
    @Override
    public String handle(JSONObject payload) {
        if (!Objects.equals(payload.get("topic").toString(), "gameSession")){
            System.out.println("shouldnt be here");
            return "Error";
        }
        else {
            String command = payload.get("command").toString();
            System.out.println(command);
            switch (command){
                case "startgame":
                    try{
                        String answer = payload.get("answer").toString();
                        if (answer.equals("confirm")||answer.equals("deny")) {
                            return "game request answer";
                        }
                        else {return "Error, malformed answer to game request";}
                    } catch (Exception e){
                        String playerId = payload.get("playerId").toString();
                        if (playerId != null){
                            System.out.println("game wanted");
                            return "startgame";
                        }
                        else {return "Error, no playerID for startgame";}
                    }
                case "gameState":
                    try {
                        String info = payload.get("info").toString();
                        if (info.equals("whoseTurn")) {
                            System.out.println("whoseTurn wanted");
                            return "turnInfo";
                        }
                        else if (info.equals("boardState")){
                            System.out.println("boardState wanted");
                            return "boardState";
                        }
                        else { return "Error, not a proper info request";}
                    } catch (Exception e) {
                        return "Error, no proper gameState request";
                    }
                case "quitgame":
                    try {
                        String state = payload.get("state").toString();
                        if(state.equals("initiate")){
                            System.out.println("Player wants out");
                            return "quitgame init";
                        }
                        else if(state.equals("confirm")){
                            System.out.println("quitgame confirmed");
                            return "quitgame confirm";
                        }
                        else { return "Error, not a proper quitgame request";}
                    } catch (Exception e){
                        return "Error, not a proper quitgame request";
                    }
            }
        }
        return "Error";
    }

}
