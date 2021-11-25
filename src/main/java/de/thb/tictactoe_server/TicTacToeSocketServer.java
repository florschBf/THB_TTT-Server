package de.thb.tictactoe_server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class TicTacToeSocketServer extends WebSocketServer {

    public TicTacToeSocketServer(InetSocketAddress address) {
        super(address);
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
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        switch(message){
            case "1":
                System.out.println("placing a 1");
                conn.send("I marked the 1 for you");
                break;
            case "2":
                System.out.println("placing a 2");
                conn.send("I marked the 2 for you");
                break;
            case "3":
                System.out.println("placing a 3");
                conn.send("I marked the 3 for you");
                break;
            case "4":
                System.out.println("placing a 4");
                conn.send("I marked the 4 for you");
                break;
            case "5":
                System.out.println("placing a 5");
                conn.send("I marked the 5 for you");
                break;
            case "6":
                System.out.println("placing a 6");
                conn.send("I marked the 6 for you");
                break;
            case "7":
                System.out.println("placing a 7");
                conn.send("I marked the 7 for you");
                break;
            case "8":
                System.out.println("placing a 8");
                conn.send("I marked the 8 for you");
                break;
            case "9":
                System.out.println("placing a 9");
                conn.send("I marked the 9 for you");
                break;
            default:
                System.out.println("err");
                conn.send("You said " + message);
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
        System.out.println("TicTacToe socket server started successfully on port " + this.getPort());
        this.setConnectionLostTimeout(0);
        this.setConnectionLostTimeout(10000);
    }
}

