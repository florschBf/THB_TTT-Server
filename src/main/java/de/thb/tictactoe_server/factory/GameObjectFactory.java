package de.thb.tictactoe_server.factory;

import de.thb.tictactoe_server.gameobject.GameObject;
import de.thb.tictactoe_server.gameobject.Player;

import java.util.Objects;

/**
 * Factory for gameObjects, currently wip - might be unnecessary
 */

public class GameObjectFactory {

    public GameObject getGameObject(String objectType){

        if (Objects.equals(objectType, "Player")){
            return new Player();
        }
        else{
            return null;
        }
    }
}
