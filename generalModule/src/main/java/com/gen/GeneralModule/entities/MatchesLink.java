package com.gen.GeneralModule.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class MatchesLink {
    @Id
    public int id;
    public String matchUrl;
    public String leftTeam;
    public String rightTeam;
}
