package com.gen.GeneralModule.dtos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchesDto {
    public Integer id;
    public String matchesUrl;
    public String leftTeam;
    public String rightTeam;
    public String matchFormat;
    public String matchDate;
    public List<String> matchMapsNames;
    public String leftTeamOdds;
    public String rightTeamOdds;
    public Integer matchTime;
    public Map<String, String> mapsPredict = new HashMap<>(); //название карты к строчке, в которой название выигравшей команды. Может быть пустым
    public List<String> mapsPredictChanged = new ArrayList<>(); //for front
    public Integer alreadyBet;
    public Integer betLimit;
    public Boolean dontShow;
}
