package com.epam.mentoring.multithreading.architecture.task5.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {

    private String id;

    private List<CurrencyAccount> currencyAccounts;

}
