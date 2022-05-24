package org.mainModule.entities;

public class Player {
    public String id;
    public String url;
    public String name;

    //игровые характеристики
    public int stability; //стабильность игрока
    public PlayerInMapResults dust2Results;
    public PlayerInMapResults mirageResults;
    public PlayerInMapResults infernoResults;
    public PlayerInMapResults nukeResults;
    public PlayerInMapResults overpassResults;
    public PlayerInMapResults vertigoResults;
    public PlayerInMapResults ancientResults;

    public Player(){
        this.stability = 0;
        this.dust2Results = new PlayerInMapResults();
        this.mirageResults = new PlayerInMapResults();
        this.infernoResults = new PlayerInMapResults();
        this.nukeResults = new PlayerInMapResults();
        this.overpassResults = new PlayerInMapResults();
        this.vertigoResults = new PlayerInMapResults();
        this.ancientResults = new PlayerInMapResults();
    }
}
