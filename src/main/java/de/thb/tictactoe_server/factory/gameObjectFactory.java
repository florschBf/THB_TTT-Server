package de.thb.tictactoe_server.factory;

import de.thb.tictactoe_server.gameobject.GameObject;
import de.thb.tictactoe_server.gameobject.Player;

import java.util.Objects;

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
