package com.gen.GeneralModule.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class ResultsLink {
    @Id
    @SequenceGenerator(name = "sq_results_link", sequenceName = "sq_results_link_id", allocationSize = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_results_link")
    public int id;
    public String matchUrl;
    public Boolean processed;
    public Boolean archive;
}
