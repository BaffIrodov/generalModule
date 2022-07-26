package com.gen.GeneralModule.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
public class RoundHistory {
    @Id
    public Integer idStatsMap; //id stats-страницы
    public Date dateOfMatch; //дата матча
    public String roundSequence; //последовательность раундов - (L, L, L, R, R) - первая тима выигрывает 3, вторая выигрывает 2 раунда

//    public RoundHistory(){
//        this.idStatsMap = "";
//        this.dateOfMatch = null;
//        this.roundSequence = new ArrayList<>();
//    }
//
//    public boolean validateThisObject(){
//        return !this.idStatsMap.equals("") &&
//                this.dateOfMatch != null &&
//                this.roundSequence.size() > 0;
//    }
//
//    public RoundHistory returnValidatedObjectOrNull(){
//        if(validateThisObject()){
//            return this;
//        } else {
//            return null;
//        }
//    }
}
