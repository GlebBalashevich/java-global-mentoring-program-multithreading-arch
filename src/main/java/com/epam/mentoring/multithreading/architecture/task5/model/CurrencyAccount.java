package com.epam.mentoring.multithreading.architecture.task5.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyAccount {

    private Currency currency;

    private BigDecimal amount;

}
