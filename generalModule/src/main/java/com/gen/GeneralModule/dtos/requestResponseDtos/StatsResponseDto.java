package com.gen.GeneralModule.dtos.requestResponseDtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class StatsResponseDto {
    public Integer batchSize;
    public Integer batchTime;
    public Date requestDate;
}
