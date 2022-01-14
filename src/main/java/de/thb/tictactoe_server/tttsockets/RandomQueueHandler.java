package de.thb.tictactoe_server.tttsockets;

import de.thb.tictactoe_server.gameobject.Player;

import java.util.ArrayList;

public class RandomQueueHandler {
    private final ArrayList<Player> randomQueue = new ArrayList<>();

    /**
     * Methode zum Hinzufügen von Spielern in die Zufallsspielliste
     * Speichert auch das mitgegebene Icon beim Player-Objekt
     * @param player Der hinzuzufügende Spieler als Player-Objekt
     */
    public void addPlayerToQueue(Player player){
        this.randomQueue.add(player);

        startGameWithRandom(player);
    }

    public void removePlayerFromQueue(Player player){
        this.randomQueue.remove(player);
    }

    public void startGameWithRandom(Player p1){
        if (countQueue() > 1){
            //we got someone else, should be infront of p1, lets play
            Player p2 = returnFirstInQueue(); //this removes the person from the queue!
            GameSessionHandler newGame = new GameSessionHandler(p1,p2);
            newGame.initGame("gameConfirmed"); //auto confirming here, no need to query players again
            //remove player from queue, he got a match, p2 should be removed already
            removePlayerFromQueue(p1);
        }
        else{
            //no one else in queue, will have to wait for someone
            System.out.println("waiting for someone else in queue");
        }
    }

    public Player returnFirstInQueue(){
        //get Player from queue
        Player first = this.randomQueue.get(0);
        System.out.println("got an opponent for you: " + first);
        //remove Player from queue
        this.randomQueue.remove(0);
        return first;
    }

    public Long countQueue(){
        System.out.println("returning this many players in rand queue: " + this.randomQueue.stream().count());
        return this.randomQueue.stream().count();
    }
}
