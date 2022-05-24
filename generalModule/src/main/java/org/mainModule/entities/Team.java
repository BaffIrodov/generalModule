package org.mainModule.entities;;

public class Team {
    public String id;
    public String url;
    public Player player1;
    public Player player2;
    public Player player3;
    public Player player4;
    public Player player5;

    public Team(){
        this.id = "";
        this.url = "";
        this.player1 = new Player();
        this.player2 = new Player();
        this.player3 = new Player();
        this.player4 = new Player();
        this.player5 = new Player();
    }
}
