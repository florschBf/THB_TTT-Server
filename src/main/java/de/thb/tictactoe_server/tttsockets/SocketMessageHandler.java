package de.thb.tictactoe_server.tttsockets;

import de.thb.tictactoe_server.factory.GameObjectFactory;
import de.thb.tictactoe_server.gameobject.Player;
import de.thb.tictactoe_server.tttsockets.messageHandlers.*;
import org.java_websocket.WebSocket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Klasse um Nachrichten, die über den Websocketserver empfangen werden, auszuwerten
 */
public class SocketMessageHandler {
    private final SignUpMsgHandler signUpMsg = new SignUpMsgHandler();
    private final GameSessionMsgHandler sessionMsg = new GameSessionMsgHandler();
    private final MoveMsgHandler moveMsg = new MoveMsgHandler();

    /**
     * Methode um Client-Anfragen auszuwerten und Anweisungen zurückzugeben
     *
     * @param message Die erhaltene Nachricht vom Client
     * @return String aus dem der Server die nächste Aktion ableiten kann
     * @throws ParseException --> Probleme mit dem Format abfangen, damit nicht noch der ganze Server abrauscht
     */
    public String msgResult(String message) throws ParseException {

        //Annahme: TTT-Protokoll V 1.0 (Stand 01.12.'21)
        //Parse entsprechend nach den bekannten Topics und Commands.
        //Websockets lässt sonst automatisch die Connection fallen
        JSONObject payload = parseJSONString(message);
        System.out.println(payload.get("topic").toString());
        switch (payload.get("topic").toString()) {

            case "signup":
                System.out.println("signUp topic -> calling SignUpMsgHandler");
                return this.signUpMsg.handle(payload);
            case "gameSession":
                System.out.println("gameSession topic -> calling GameSessionMsgHandler");
                return this.sessionMsg.handle(payload);
            case "gameMove":
                System.out.println("gameMove topic -> calling MoveMsgHandler");
                return this.moveMsg.handle(payload);
            default:
                System.out.println("found no useful message..");
                return "You said" + payload;
        }
    }

    /**
     * JSON parser methode
     * @param message String Nachricht im JSON Format
     * @return JSONObject aus Nachricht
     * @throws ParseException --> vermutlich JSON fehlerhaft
     */
    private JSONObject parseJSONString(String message) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(message);
        JSONObject payload = (JSONObject) obj;
        return payload;
    }

    /**
     * Utility Methode um das passende Player-GameObject zur SignUp-Nachricht zu instanzieren
     *
     * @param conn    Die dazugehörige Websocket-Verbindung
     * @param message Der String mit dem sich angemeldet wurde
     * @return Fertiges Player-GameObject zum Eintragen in Liste etc.
     * @throws ParseException --> wenn mit dem JSON was schief läuft
     */
    public Player getPlayerFromMsg(WebSocket conn, String message) throws ParseException {
        JSONObject payload = parseJSONString(message);
        GameObjectFactory playerFac = new GameObjectFactory();
        Player newPlayer = (Player) playerFac.getGameObject("Player");

        newPlayer.setName((String) payload.get("player"));
        newPlayer.setFirebaseId((String) payload.get("firebaseId"));
        newPlayer.setConn(conn);
        newPlayer.setUid();
        System.out.println("added " + newPlayer.getConn());
        return newPlayer;
    }

    /**
     * Utility Methode um die PlayerUid aus Nachricht zu extrahieren
     *
     * @param message Die empfangene Nachricht
     * @return Gewünschte UID des (Gegen)spielers
     * @throws ParseException --> malformed JSON?
     */
    public Integer getPlayerUidFromMessage(String message) throws ParseException {
        try {
            //Assuming proper game request in message
            JSONObject payload = parseJSONString(message);
            System.out.println(payload);
            String uid = (String) payload.get("playerId");
            System.out.println("got him here:" + uid);
            Integer player2uid = Integer.parseInt(uid);
            System.out.println(player2uid);
            return player2uid;
        } catch (ParseException e) {
            System.out.println(e);
            return 0;
        }
    }

    /**
     * Method extracts a player icon ID from a message for transfer to opponent(s)
     * @param message the received websocket message
     * @return String of the icon ID for use in Android app
     * @throws ParseException
     */
    public String getPlayerIconIdFromMessage(String message) throws ParseException {
        System.out.println("looking for playerIcon");
        JSONObject payload = parseJSONString(message);
        if (payload.containsKey("playerIcon")){
            System.out.println("this should be it");
            String icon = payload.get("playerIcon").toString();
            System.out.println(icon);
            return icon;
        }
        else { return null; }
    }

    /**
     * Methode um Antworten auf Spieleanfragen zu verarbeiten
     * @param message String mit Antwort auf Spielanfrage nach TTT-Protokoll
     * @return String gameConfirmed|gameDenied|Error
     * @throws ParseException Wahrscheinlich Probleme mit dem JSON-Format
     */
    public String getStartGameReply(String message) throws ParseException {
        JSONObject payload = parseJSONString(message);
        if (payload.containsKey("answer")) {
            if (payload.get("answer").equals("confirm")) {
                return "gameConfirmed";
            } else {
                return "gameDenied";
            }
        }
        System.out.println("Error, not a proper game-challenge answer");
        return "Error, not a proper game-challenge answer";
    }
}
