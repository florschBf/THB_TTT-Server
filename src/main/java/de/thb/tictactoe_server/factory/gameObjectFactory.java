package de.thb.tictactoe_server.factory;

import de.thb.tictactoe_server.gameobject.GameObject;
import de.thb.tictactoe_server.gameobject.Player;

import java.util.Objects;

/**
 * Factory for gameObjects, currently wip - might be unnecessary
 */

public class gameObjectFactory {

    public GameObject getGameObject(String objectType){

        if (Objects.equals(objectType, "player")){
            return new Player();
        }
        else{
            return null;
        }
    }
}
