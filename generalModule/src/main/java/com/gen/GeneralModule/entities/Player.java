package com.gen.GeneralModule.entities;

import com.gen.GeneralModule.dtos.PlayerOnMapResultsDto;

import java.util.Date;

public class Player {
    public String id;
    public String url;
    public String name;
    public Date dateOfMatch;
    public PlayerOnMapResultsDto dust2Results;
    public PlayerOnMapResultsDto mirageResults;
    public PlayerOnMapResultsDto infernoResults;
    public PlayerOnMapResultsDto nukeResults;
    public PlayerOnMapResultsDto overpassResults;
    public PlayerOnMapResultsDto vertigoResults;
    public PlayerOnMapResultsDto ancientResults;
}
