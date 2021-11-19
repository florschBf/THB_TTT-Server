package de.thb.tictactoe_server.gameobject;

public class Player implements GameObject{
    private String name;
    private String firebaseId;
    private Long uid;

    public Player(){

    }

    public Player(String firebaseId, String name) {
        this.name = name;
        this.firebaseId = firebaseId;
    }

    public String getName(){
        return name;
    }

    public Long getUid(){
        return uid;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setUid(Long uid){
        this.uid = uid;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setFirebaseId(String firebaseId){
        this.firebaseId = firebaseId;
    }
}
