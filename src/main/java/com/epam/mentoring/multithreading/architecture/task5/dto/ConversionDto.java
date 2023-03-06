package com.epam.mentoring.multithreading.architecture.task5.dto;

import java.math.BigDecimal;

import com.epam.mentoring.multithreading.architecture.task5.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversionDto {

    private Currency base;

    private Currency target;

    private BigDecimal sellRate;

    private BigDecimal buyRate;

}
