package org.mainModule.entities;

import java.util.Date;

//Это для записи в БД
public class PlayerOnMapResultsToBD {
    public int id; //id игрока
    public String idStatsMap; //id stats-страницы
    public String url; //url игрока, вероятно, может быть удалено
    public String name; //ник игрока
    public Date dateOfMatch; //дата матча
    public MapsEnum map; //карта, на которой был сыгран матч
    public String team; //команда, в которой играет человек - left, right
    public int kills; //убийства (парсинг: целое число)
    public int assists; //помощь в убийстве (парсинг: строка вида " (8)")
    public int deaths; //смерти (парсинг: целое число)
    public float kd; //отношение киллов к смертям, (парсинга нет, считается)
    public int headshots; //количество хедшотов за карту, (парсин: целое число)
    public float adr; //АДР - средний урон, нанесенный за раунд, (парсинг: число в формате 75.1)
    public float rating20; //рейтинг 2.0, (парсинг: число в формате 1.23)
    public float cast; //каст - количество раундов, когда игрок сделал хоть что-то для победы, (парсинг: число в формате 72.3%)

    public PlayerOnMapResultsToBD(){
        this.id = 0;
        this.idStatsMap = "";
        this.url = "";
        this.name = "";
        this.dateOfMatch = null;
        this.map = MapsEnum.ALL;
        this.team = "";
        this.kills = 0;
        this.assists = 0;
        this.deaths = 0;
        this.kd = 0;
        this.headshots = 0;
        this.adr = 0;
        this.rating20 = 0;
        this.cast = 0;
    }

    //простая проверка того, сготовился ли объект игрока. Все остальные поля могут быть нулевыми - не отличаться от значений конструктора. Эти не могут
    public boolean validateThisObject(){
        return this.id != 0 &&
                !this.idStatsMap.equals("") &&
                this.dateOfMatch != null &&
                this.map != MapsEnum.ALL &&
                !this.team.equals("");
    }

    public PlayerOnMapResultsToBD returnValidatedObjectOrNull(){
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
