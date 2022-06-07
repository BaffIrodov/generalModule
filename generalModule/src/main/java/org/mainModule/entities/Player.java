package org.mainModule.entities;

public class Player {
    public String id;
    public String url;
    public String name;

    //игровые характеристики
    public int stability; //стабильность игрока
    public PlayerOnMapResults dust2Results;
    public PlayerOnMapResults mirageResults;
    public PlayerOnMapResults infernoResults;
    public PlayerOnMapResults nukeResults;
    public PlayerOnMapResults overpassResults;
    public PlayerOnMapResults vertigoResults;
    public PlayerOnMapResults ancientResults;
    public PlayerOnMapResults cacheResults;
    public PlayerOnMapResults trainResults;

    public Player(){
        this.stability = 0;
        this.dust2Results = null;
        this.mirageResults = null;
        this.infernoResults = null;
        this.nukeResults = null;
        this.overpassResults = null;
        this.vertigoResults = null;
        this.ancientResults = null;
    }
}
