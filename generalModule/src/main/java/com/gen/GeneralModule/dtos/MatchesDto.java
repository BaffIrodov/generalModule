package com.gen.GeneralModule.dtos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchesDto {
    public Integer id;
    public String matchesUrl;
    public String leftTeam;
    public String rightTeam;
    public String matchFormat;
    public List<String> matchMapsNames;
    public String leftTeamOdds;
    public String rightTeamOdds;
    public Integer matchTime;
    public Map<String, String> mapsPredict = new HashMap<>(); //название карты к строчке, в которой название выигравшей команды. Может быть пустым
}
