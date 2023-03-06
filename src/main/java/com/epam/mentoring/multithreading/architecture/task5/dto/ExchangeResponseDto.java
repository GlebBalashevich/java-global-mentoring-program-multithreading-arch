package com.epam.mentoring.multithreading.architecture.task5.dto;

import java.math.BigDecimal;

import com.epam.mentoring.multithreading.architecture.task5.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeResponseDto {

    private Currency base;

    private BigDecimal withdrawAmount;

    private Currency target;

    private BigDecimal targetAmount;

}
