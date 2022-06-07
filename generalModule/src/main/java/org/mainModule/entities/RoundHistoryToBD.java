package org.mainModule.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoundHistoryToBD {
    public int idStatsMap; //id stats-страницы
    public Date dateOfMatch; //дата матча
    public List<String> roundSequence; //последовательность раундов - (L, L, L, R, R) - первая тима выигрывает 3, вторая выигрывает 2 раунда

    public RoundHistoryToBD(){
        this.idStatsMap = 0;
        this.dateOfMatch = null;
        this.roundSequence = new ArrayList<>();
    }

    public boolean validateThisObject(){
        return this.idStatsMap != 0 && this.dateOfMatch != null && this.roundSequence.size() > 0;
    }
}
