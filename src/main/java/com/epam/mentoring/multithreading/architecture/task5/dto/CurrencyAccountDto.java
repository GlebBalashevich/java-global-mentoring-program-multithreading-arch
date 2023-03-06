package com.epam.mentoring.multithreading.architecture.task5.dto;

import java.math.BigDecimal;

import com.epam.mentoring.multithreading.architecture.task5.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyAccountDto {

    private Currency currency;

    private BigDecimal amount;

}
