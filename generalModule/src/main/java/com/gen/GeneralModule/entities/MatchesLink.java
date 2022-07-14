package com.gen.GeneralModule.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class MatchesLink {
    @Id
    //@SequenceGenerator(name = "sq_matches_link", sequenceName = "sq_matches_link_id", allocationSize = 0)
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_matches_link")
    public int id;
    public String matchUrl;
}
