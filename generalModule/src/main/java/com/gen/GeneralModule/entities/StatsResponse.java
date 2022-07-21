package com.gen.GeneralModule.entities;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor
public class StatsResponse {
    @Id
    @SequenceGenerator(name = "sq_stats_response", sequenceName = "sq_stats_response_id", allocationSize = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_stats_response")
    public int id;
    public Integer batchSize;
    public Integer batchTime;
    public Date requestDate;
}
