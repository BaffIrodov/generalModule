package com.gen.GeneralModule.dtos;

public class PlayerDto {
    public String id;

    public String name;

    //игровые характеристики
    public int stability; //стабильность игрока
    public PlayerOnMapResultsDto dust2Results;
    public PlayerOnMapResultsDto mirageResults;
    public PlayerOnMapResultsDto infernoResults;
    public PlayerOnMapResultsDto nukeResults;
    public PlayerOnMapResultsDto overpassResults;
    public PlayerOnMapResultsDto vertigoResults;
    public PlayerOnMapResultsDto ancientResults;
    public PlayerOnMapResultsDto cacheResults;
    public PlayerOnMapResultsDto trainResults;

    public PlayerDto(){
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
