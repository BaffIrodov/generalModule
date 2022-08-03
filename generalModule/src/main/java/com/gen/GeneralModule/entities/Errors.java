package com.gen.GeneralModule.entities;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor
public class Errors {
    @Id
    @SequenceGenerator(name = "sq_errors", sequenceName = "sq_errors_id", allocationSize = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_errors")
    public int Id;
    public String classAndLine;
    public String descriptionError;
    public Boolean verificationError;
    public String payload;
    public Date dateTime;
}
