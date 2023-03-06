package com.epam.mentoring.multithreading.architecture.task5.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conversion {

    private Currency base;

    private Currency target;

    private BigDecimal sellRate;

    private BigDecimal buyRate;

}
