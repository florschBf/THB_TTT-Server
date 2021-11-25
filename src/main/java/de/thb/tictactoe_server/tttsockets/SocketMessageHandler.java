package de.thb.tictactoe_server.tttsockets;

import de.thb.tictactoe_server.factory.GameObjectFactory;
import de.thb.tictactoe_server.gameobject.Player;
import org.java_websocket.WebSocket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Klasse um Nachrichten, die über Websocket empfangen werden, auszuwerten
 */
public class SocketMessageHandler {

    public String msgResult(String message) throws ParseException {
        switch(message) {
            case "1":
                System.out.println("placing a 1");
                return ("Feld 1 gesetzt");
            case "2":
                System.out.println("placing a 2");
                return ("Feld 2 gesetzt");
            case "3":
                System.out.println("placing a 3");
                return ("Feld 3 gesetzt");
            case "4":
                System.out.println("placing a 4");
                return ("Feld 4 gesetzt");
            case "5":
                System.out.println("placing a 5");
                return ("Feld 5 gesetzt");
            case "6":
                System.out.println("placing a 6");
                return ("Feld 6 gesetzt");
            case "7":
                System.out.println("placing a 7");
                return ("Feld 7 gesetzt");
            case "8":
                System.out.println("placing a 8");
                return ("Feld 8 gesetzt");
            case "9":
                System.out.println("placing a 9");
                return ("Feld 9 gesetzt");
            default:
                try{
                    //Assuming logon JSON data in message as no ttt number
                    JSONObject payload = parseJSONString(message);
                    System.out.println(payload);
                    if(payload.get("name") != null){
                        //Player just connected, needs to be added to List
                        return "add player called";
                    }
                    else if(payload.get("topic").equals("startgame")){
                        System.out.println("game wanted");
                        return "startgame";
                    }
                    System.out.println(payload.get("name"));
                    return ("You said " + payload);
                }
                catch (ParseException exception){
                    //No JSON, taking it for simple String
                    return ("You said " + message);
                }
        }
    }

    private JSONObject parseJSONString(String message) throws ParseException{
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(message);
        JSONObject payload = (JSONObject) obj;
        return payload;
    }

    public Player getPlayerFromMsg(WebSocket conn, String message) throws ParseException {
        JSONObject payload = parseJSONString(message);
        GameObjectFactory playerFac = new GameObjectFactory();
        Player newPlayer = (Player) playerFac.getGameObject("Player");
        newPlayer.setName((String)payload.get("name"));
        newPlayer.setFirebaseId((String) payload.get("firebaseId"));
        newPlayer.setConn(conn);
        newPlayer.setUid();
        System.out.println("added " + newPlayer.getConn());
        return newPlayer;
    }

    public Integer getPlayerUidFromMessage(String message) throws ParseException{
        try {
            //Assuming proper game request in message
            JSONObject payload = parseJSONString(message);
            System.out.println(payload);
            String uid = (String) payload.get("uid");
            System.out.println("got him here:" + uid);
            Integer player2uid = Integer.parseInt(uid);
            System.out.println(player2uid);
            return player2uid;
        }
        catch (ParseException e){
            System.out.println(e);
            return 0;
        }
    }


}
