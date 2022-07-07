package com.gen.GeneralModule.dtos;

public class PlayerOnMapResultsDto {
    public int kills; //убийства (парсинг: целое число)
    public int assists; //помощь в убийстве (парсинг: строка вида " (8)")
    public int deaths; //смерти (парсинг: целое число)
    public float kd; //отношение киллов к смертям, (парсинга нет, считается)
    public int headshots; //количество хедшотов за карту, (парсин: целое число)
    public float adr; //АДР - средний урон, нанесенный за раунд, (парсинг: число в формате 75.1)
    public float rating20; //рейтинг 2.0, (парсинг: число в формате 1.23)
    public float cast; //каст - количество раундов, когда игрок сделал хоть что-то для победы, (парсинг: число в формате 72.3%)

    public PlayerOnMapResultsDto(){
        this.kills = 0;
        this.assists = 0;
        this.deaths = 0;
        this.kd = 0;
        this.headshots = 0;
        this.adr = 0;
        this.rating20 = 0;
        this.cast = 0;
    }

    public void calculateKD(){
        this.kd = (float) this.kills / (float) this.deaths;
    }
}
