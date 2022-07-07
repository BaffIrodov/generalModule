package com.gen.GeneralModule.dtos;

public class Team {
    public String id;
    public String url;
    public PlayerDto player1;
    public PlayerDto player2;
    public PlayerDto player3;
    public PlayerDto player4;
    public PlayerDto player5;

    public Team(){
        this.id = "";
        this.url = "";
        this.player1 = new PlayerDto();
        this.player2 = new PlayerDto();
        this.player3 = new PlayerDto();
        this.player4 = new PlayerDto();
        this.player5 = new PlayerDto();
    }
}
