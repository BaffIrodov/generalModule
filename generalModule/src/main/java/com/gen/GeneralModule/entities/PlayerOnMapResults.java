package com.gen.GeneralModule.entities;

import com.gen.GeneralModule.common.MapsEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

//Это для записи в БД

@Entity
@AllArgsConstructor
public class PlayerOnMapResults {
    @Id
    @SequenceGenerator(name = "sq_player_on_map_results", sequenceName = "sq_player_on_map_results_id", allocationSize = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_player_on_map_results")
    public int id; //id записи

    public int playerId; //id игрока
    public int idStatsMap; //id stats-страницы
    public String url; //url игрока, вероятно, может быть удалено
    public String playerName; //ник игрока
    public Date dateOfMatch; //дата матча
    public MapsEnum playedMap; //карта, на которой был сыгран матч
    public String playedMapString; //карта в удобном виде
    public String team; //команда, в которой играет человек - left, right
    public int kills; //убийства (парсинг: целое число)
    public int assists; //помощь в убийстве (парсинг: строка вида " (8)")
    public int deaths; //смерти (парсинг: целое число)
    public float kd; //отношение киллов к смертям, (парсинга нет, считается)
    public int headshots; //количество хедшотов за карту, (парсин: целое число)
    public float adr; //АДР - средний урон, нанесенный за раунд, (парсинг: число в формате 75.1)
    public float rating20; //рейтинг 2.0, (парсинг: число в формате 1.23)
    public float cast20; //каст - количество раундов, когда игрок сделал хоть что-то для победы, (парсинг: число в формате 72.3%)

    public PlayerOnMapResults(){
        this.playerId = 0;
        this.idStatsMap = 0;
        this.url = "";
        this.playerName = "";
        this.dateOfMatch = null;
        this.playedMap = MapsEnum.ALL;
        this.playedMapString = MapsEnum.ALL.toString();
        this.team = "";
        this.kills = 0;
        this.assists = 0;
        this.deaths = 0;
        this.kd = 0;
        this.headshots = 0;
        this.adr = 0;
        this.rating20 = 0;
        this.cast20 = 0;
    }

    //простая проверка того, сготовился ли объект игрока. Все остальные поля могут быть нулевыми - не отличаться от значений конструктора. Эти не могут
    public boolean validateThisObject(){
        return this.playerId != 0 &&
                this.idStatsMap != 0 &&
                this.dateOfMatch != null &&
                this.playedMap != MapsEnum.ALL &&
                this.playedMapString != "ALL" &&
                !this.team.equals("");
    }

    public PlayerOnMapResults returnValidatedObjectOrNull(){
        if(validateThisObject()){
            return this;
        } else {
            return null;
        }
    }

    public void calculateKD(){
        this.kd = (float) this.kills / (float) this.deaths;
    }
}
