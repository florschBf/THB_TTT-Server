package de.thb.tictactoe_server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import de.thb.tictactoe_server.gameobject.Player;
import de.thb.tictactoe_server.tttsockets.*;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.parser.ParseException;

public class TicTacToeSocketServer extends WebSocketServer {
    private final SocketMessageHandler messageHandler;
    private final SocketLogOnHandler logOnHandler;

    public TicTacToeSocketServer(InetSocketAddress address) {
        super(address);
        this.messageHandler = new SocketMessageHandler();
        this.logOnHandler = new SocketLogOnHandler();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the server!"); //This method sends a message to the new client
        broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
        System.out.println("new connection to " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
        this.logOnHandler.removePlayer(conn);
        broadcast(logOnHandler.returnPlayers());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        String handledMessage = null;
        try {
            handledMessage = messageHandler.msgResult(message);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        switch (handledMessage){
            case ("add player called"):
                try {
                    addPlayerToList(this.messageHandler.getPlayerFromMsg(conn, message));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                broadcast(logOnHandler.returnPlayers());
                break;
            case ("startgame"):
                Player player1 = logOnHandler.getPlayerByConn(conn);
                if(player1.getInGame()){
                    conn.send("Error, you're in a game already"); //TODO Protokoll für Fehlermeldungen?
                }
                else{
                    Player player2 = null;
                    try {
                        player2 = logOnHandler.getPlayerByUid(messageHandler.getPlayerUidFromMessage(message));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        break;
                    }
                    GameSessionHandler session = new GameSessionHandler(player1, player2);
                    //markiere Spieler als beschäftigt in der Liste der Clients
                    broadcast(logOnHandler.setPlayerAsBusy(player1));
                    broadcast(logOnHandler.setPlayerAsBusy(player2));
                }
                break;
            case ("game request answer"):
                //suche passende Gamesession über den conn Websocket und rufe dort "initGame" mit der message auf
                try {
                    Player p1 = logOnHandler.getPlayerByConn(conn);
                    Player p2 = p1.getGameSession().getPlayer2();
                    if(logOnHandler.getPlayerByConn(conn).getGameSession().initGame(messageHandler.getStartGameReply(message))){
                        //Game started successfully, dont have to do anything
                        break;
                    }
                    else {
                        //Game was denied or failed, Gamesession returned false or nothing
                        //Spieler müssen wieder freigegeben werden
                        broadcast(logOnHandler.setPlayerAsFree(p1));
                        broadcast(logOnHandler.setPlayerAsFree(p2));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            default:
                System.out.println("didn't understand the message");
                conn.send("Error, malformed message, stick to protocol");
        }
/*        if(handledMessage.equals("add player called")){
            try {
                addPlayerToList(this.messageHandler.getPlayerFromMsg(conn, message));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            broadcast(logOnHandler.returnPlayers());
        }
        else {
            Player player1 = logOnHandler.getPlayerByConn(conn);
            if(!player1.getInGame()){
                switch(handledMessage){
                    case("startgame"):
                        Player player2 = null;
                        try {
                            player2 = logOnHandler.getPlayerByUid(messageHandler.getPlayerUidFromMessage(message));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        GameSessionHandler session = new GameSessionHandler(player1, player2);
                        //markiere Spieler als beschäftigt in der Liste der Clients
                        broadcast("{\"topic\":\"signup\",\"players\":"+player1.getUid().toString()+" is busy\"}");
                        broadcast("{\"topic\":\"signup\",\"players\":"+player2.getUid().toString()+" is busy\"}");
                        break;
                    default: conn.send(handledMessage);
                }
            }
            else if (player1.getInGame()){
                GameSessionHandler session = player1.getGameSession();
                switch(handledMessage){
                    case("Feld 1 gesetzt"):
                        session.move(conn,1);
                        break;
                    case("Feld 2 gesetzt"):
                        session.move(conn,2);
                        break;
                    case("Feld 3 gesetzt"):
                        session.move(conn,3);
                        break;
                    case("Feld 4 gesetzt"):
                        session.move(conn,4);
                        break;
                    case("Feld 5 gesetzt"):
                        session.move(conn,5);
                        break;
                    case("Feld 6 gesetzt"):
                        session.move(conn,6);
                        break;
                    case("Feld 7 gesetzt"):
                        session.move(conn,7);
                        break;
                    case("Feld 8 gesetzt"):
                        session.move(conn,8);
                        break;
                    case("Feld 9 gesetzt"):
                        session.move(conn,9);
                        break;
                    default:
                        conn.send("Dunno what you're sending, play your game.");
                }
            }
        }*/
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        System.out.println("received ByteBuffer from "	+ conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
    }

    @Override
    public void onStart() {
        System.out.println("TicTacToe socket server started successfully on port " + this.getPort());
        this.setConnectionLostTimeout(0);
        this.setConnectionLostTimeout(10000);
    }

    public void addPlayerToList(Player newPlayer){
        this.logOnHandler.addConnToPlayerList(newPlayer);
    }

    private void unbusyPlayers(Boolean gameFailed, Player p1, Player p2){

    }
}

