package com.gen.GeneralModule.entities;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class BetCondition {
    @Id
    public int matchId;
    public int alreadyBet;
    public int betLimit;
    public boolean dontShow;
    public boolean itWasWon;
}
