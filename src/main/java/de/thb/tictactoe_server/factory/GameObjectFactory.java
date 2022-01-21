package de.thb.tictactoe_server.factory;

import de.thb.tictactoe_server.gameobject.GameObject;
import de.thb.tictactoe_server.gameobject.Player;

import java.util.Objects;

/**
 * Factory for gameObjects, currently wip - might be unnecessary
 */
public class GameObjectFactory {

    /**
     * Method to return requested gameObjects, e.g. Player objects
     * @param objectType String defines wanted gameObject, e.g. Player
     * @return The desired object, e.g. Player
     */
    public GameObject getGameObject(String objectType){
        if (Objects.equals(objectType, "Player")){
            return new Player();
        }
        else{
            return null;
        }
    }
}
