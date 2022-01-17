package de.thb.tictactoe_server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import de.thb.tictactoe_server.gameobject.Player;
import de.thb.tictactoe_server.tttsockets.*;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.parser.ParseException;

/**
 * WebSocketServer main class - all threads connect here :-)
 * contains a SocketMessageHandler to handle incoming messages
 * contains a SockerLogOnHandler to handle different websocket client connections
 * contains a RandomQueueHandler for matchmaking purposes
 */
public class TicTacToeSocketServer extends WebSocketServer {
    private final SocketMessageHandler messageHandler;
    private final SocketLogOnHandler logOnHandler;
    private final RandomQueueHandler randomQueue;

    public TicTacToeSocketServer(InetSocketAddress address) {
        super(address);
        this.messageHandler = new SocketMessageHandler();
        this.logOnHandler = new SocketLogOnHandler();
        this.randomQueue = new RandomQueueHandler();
    }

    /**
     * Method called on opening of a new websocket connection - not doing much, waiting for connection to register the player accoring to TTT-protocol 2.0
     * -> happens in onMessage method
     * @param conn the opened websocket connection
     * @param handshake handshake data
     */
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the server!"); //This method sends a message to the new client
        broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
        System.out.println("new connection to " + conn.getRemoteSocketAddress());
    }

    /**
     * Method called on close of a websocket conn
     * @param conn the closed connection to process
     * @param code websocket status code
     * @param reason given reason
     * @param remote irrelevant ;-)
     */
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
        //TODO handle disconnects on Players who are in a gamesession.
        //e.g. check whether player is in game and if he is, get his gamesession to properly inform player2 and quit the thing
        //who is disconnecting? need to know!
        Player player = this.logOnHandler.getPlayerByConn(conn);
        if (player.getInGame()){
            System.out.println("Disconnect in a game, ohoh");
            player.getGameSession().quitGameDisconnect(player);
        }
        this.logOnHandler.removePlayer(player);
        broadcast(logOnHandler.returnPlayers());
    }

    /**
     * Method called on received websocket message, main handler of client and game interactions
     * ***CHANGES HERE POTENTIALLY BREAK ALL CLIENT INTERACTIONS***
     * @param conn the websocket connection that send the message
     * @param message String that arrived - needs to follow TTT-protocol 2.0
     */
    @Override
    public void onMessage(WebSocket conn, String message) {
        String handledMessage = null;

        try {
            handledMessage = messageHandler.msgResult(message);
        } catch (ParseException e) {
            System.out.println("Error processing the incoming message. Pretty fundamental... should not have happened ;-)");
            e.printStackTrace();
        }
        switch (handledMessage){
                // TOPIC SIGNUP RESPONSES
            case ("add player called"):
                try {
                    addPlayerToList(this.messageHandler.getPlayerFromMsg(conn, message));
                    conn.send(logOnHandler.informPlayerOfUID(conn));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                broadcast(logOnHandler.returnPlayers());
                break;
            case ("delete player called"):
                try {
                    System.out.println("removing player from list");
                    logOnHandler.removePlayer(conn);
                }
                catch (Exception e){
                    System.out.println("couldnt remove player?");
                    e.printStackTrace();
                }
                broadcast(logOnHandler.returnPlayers());
                break;
            case ("playerList called"):
                broadcast(logOnHandler.returnPlayers());

                // TOPIC GAMESESSION RESPONSES
            case ("startgame"):
                Player player1 = logOnHandler.getPlayerByConn(conn);
                //update player icon, while we have it
                try {
                    player1.setIcon(messageHandler.getPlayerIconIdFromMessage(message));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(player1.getInGame()){
                    conn.send("Error, you're in a game already"); //sollte am Client bereits verhindert worden sein
                    break;
                }
                else{
                    Player player2 = null;
                    try {
                        player2 = logOnHandler.getPlayerByUid(messageHandler.getPlayerUidFromMessage(message));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        conn.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"now\",\"reason\":\"opponentDisco\"}");
                        break;
                    }
                    GameSessionHandler session = new GameSessionHandler(player1, player2);
                    //sending challenge to player2, telling p1 to wait
                    conn.send("{\"topic\":\"gameSession\",\"command\":\"startgame\",\"state\":\"hold\"}");
                    player2.getConn().send("{\"topic\":\"gameSession\",\"command\":\"startgame\",\"state\":\"challenged\",\"opponent\":\"" + player1.getName() + "\"}");
                    //markiere Spieler als beschäftigt in der Liste der Clients
                    broadcast(logOnHandler.setPlayerAsBusy(player1));
                    broadcast(logOnHandler.setPlayerAsBusy(player2));
                }
                break;
            case ("startRandom"):
                Player requestee = logOnHandler.getPlayerByConn(conn);
                try {
                    requestee.setIcon(messageHandler.getPlayerIconIdFromMessage(message));
                } catch (ParseException e) {
                    e.printStackTrace();
                    requestee.setIcon("0");
                }
                randomQueue.addPlayerToQueue(requestee);
                //this automatically triggers games for the client in RandomQueueHandler if someone else wants to play
                break;
            case ("stopRandom"):
                randomQueue.removePlayerFromQueue(logOnHandler.getPlayerByConn(conn));
                break;
            case ("game request answer"):
                //suche passende Gamesession und rufe dort "initGame" mit der message auf
                try {
                    //IMPORTANT: Get players here before the gamesession is dropped - need them for potential broadcast as free
                    //Game request answers are ALWAYS from player2 in the gamesession, because p1 initiated, thus we get them like this:
                    Player p2 = logOnHandler.getPlayerByConn(conn); //player2 send the answer
                    Player p1 = p2.getGameSession().getPlayer1(); //getting the original request origin player from the session
                    String answer = messageHandler.getStartGameReply(message);
                    if(answer.equals("gameConfirmed")){
                        //set p2 icon from message
                        p2.setIcon(messageHandler.getPlayerIconIdFromMessage(message));
                        //Game confirmed, init the game
                        p2.getGameSession().initGame(answer);
                    }
                    else {
                        //Game was denied or failed, Gamesession returned false or nothing
                        //Spieler müssen wieder freigegeben werden - rest of cleanup in GameSessionHandler initGame method
                        broadcast(logOnHandler.setPlayerAsFree(p1));
                        broadcast(logOnHandler.setPlayerAsFree(p2));
                        p1.getConn().send("{\"topic\":\"gameSession\",\"command\":\"startgame\",\"state\":\"denied\"}");
                        //not sending to p2, client handled things already, doesn't need confirmation
                        //conn.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"now\",\"reason\":\"opponentDisco\"}");
                    }
                } catch (ParseException e) {
                    System.out.println("Error on game request answer - something wrong with the format.");
                    e.printStackTrace();
                }
                break;
            case("turnInfo"):
                System.out.println("giving out turnInfo");
                Player player = logOnHandler.getPlayerByConn(conn);
                GameSessionHandler session = player.getGameSession();
                session.setPlayerReady(player);
                session.tellTurns();
                break;
            case("quitgame init"):
                Player initQuitP = logOnHandler.getPlayerByConn(conn);
                Player potentialOtherPlayer = initQuitP.getGameSession().getPlayer2();
                //Need to find out who is who
                if (initQuitP != potentialOtherPlayer){
                    //can confirm, got the other connection, do nth
                }
                else {
                    //got wrong one, switching
                    potentialOtherPlayer = initQuitP.getGameSession().getPlayer1();
                }
                //tell players that game is over and free up players from game connection
                potentialOtherPlayer.getConn().send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"now\",\"reason\":\"opponentDisco\"}");
                potentialOtherPlayer.setInGame(false);
                potentialOtherPlayer.setGameSession(null);
                conn.send("{\"topic\":\"gameSession\",\"command\":\"quitgame\",\"state\":\"confirmed\"}");
                initQuitP.setGameSession(null);
                initQuitP.setInGame(false);
                break;
                //TOPIC GAMEMOVE RESPONSES
            case("Feld 0 gesetzt"):
                // Tell gamesession it's a player move to validate and execute
                //GameSessionHandler also informs clients/players
                //TODO gamesession lookup in every case is awkward but session on Player cannot be guaranteed @start of method
                //could slice String to "Feld" and go from there to check the number to optimize...
                //--> session creation @startgame is possible, shouldn't interfere there
                logOnHandler.getPlayerByConn(conn).getGameSession().move(conn,0);
                break;
            case("Feld 1 gesetzt"):
                logOnHandler.getPlayerByConn(conn).getGameSession().move(conn,1);
                break;
            case("Feld 2 gesetzt"):
                logOnHandler.getPlayerByConn(conn).getGameSession().move(conn,2);
                break;
            case("Feld 3 gesetzt"):
                logOnHandler.getPlayerByConn(conn).getGameSession().move(conn,3);
                break;
            case("Feld 4 gesetzt"):
                logOnHandler.getPlayerByConn(conn).getGameSession().move(conn,4);
                break;
            case("Feld 5 gesetzt"):
                logOnHandler.getPlayerByConn(conn).getGameSession().move(conn,5);
                break;
            case("Feld 6 gesetzt"):
                logOnHandler.getPlayerByConn(conn).getGameSession().move(conn,6);
                break;
            case("Feld 7 gesetzt"):
                logOnHandler.getPlayerByConn(conn).getGameSession().move(conn,7);
                break;
            case("Feld 8 gesetzt"):
                logOnHandler.getPlayerByConn(conn).getGameSession().move(conn,8);
                break;
            default:
                System.out.println("didn't understand the message");
                conn.send("Error, malformed message, stick to protocol. Your message was: " + message);
        }
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
        System.out.println("TicTacToe socket server started successfully on " +  this.getAddress());
        this.setConnectionLostTimeout(0);
        this.setConnectionLostTimeout(10000);
    }

    /**
     * Method to add a Player object to the PlayerList
     * Calls SocketLogOnHandler for actual handling
     * @param newPlayer the Player object to add to the list
     */
    public void addPlayerToList(Player newPlayer){
        this.logOnHandler.addConnToPlayerList(newPlayer);
    }

    private void unbusyPlayers(Boolean gameFailed, Player p1, Player p2){

    }
}

