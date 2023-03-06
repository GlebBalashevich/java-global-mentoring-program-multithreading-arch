package com.epam.mentoring.multithreading.architecture.task5.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountDto {

    private String id;

    private List<CurrencyAccountDto> currencyAccounts;

}
