package com.gen.GeneralModule.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class ResultsLink {
//    @Id
//    @SequenceGenerator(name = "sq_results_link", sequenceName = "sq_results_link_id", allocationSize = 0)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_results_link")
//    public int id;
    @Id
    public int resultId;
    public String resultUrl;
    public Boolean processed;
    public Boolean archive;
}
