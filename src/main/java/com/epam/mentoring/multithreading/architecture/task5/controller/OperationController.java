package com.epam.mentoring.multithreading.architecture.task5.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.epam.mentoring.multithreading.architecture.task5.dto.ConversionDto;
import com.epam.mentoring.multithreading.architecture.task5.dto.CurrencyAccountDto;
import com.epam.mentoring.multithreading.architecture.task5.dto.ExchangeResponseDto;
import com.epam.mentoring.multithreading.architecture.task5.dto.UserAccountDto;
import com.epam.mentoring.multithreading.architecture.task5.model.Currency;
import com.epam.mentoring.multithreading.architecture.task5.service.ConversionService;
import com.epam.mentoring.multithreading.architecture.task5.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OperationController {

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final ConversionService conversionService;

    private final UserAccountService userAccountService;

    public ConversionDto upsertConversion(ConversionDto conversionDto) {
        return executeTask(() -> conversionService.upsertConversion(conversionDto));
    }

    public ConversionDto findConversionByCurrencies(Currency base, Currency target) {
        return executeTask(() -> conversionService.findConversionByCurrencies(base, target));
    }

    public UserAccountDto createUserAccount(List<CurrencyAccountDto> currencyAccountDtos){
        return executeTask(() -> userAccountService.createUserAccount(currencyAccountDtos));
    }

    public ExchangeResponseDto exchangeCurrency(String id, Currency base, Currency target, BigDecimal targetAmount) {
        return executeTask(() -> userAccountService.exchangeCurrency(id, base, target, targetAmount));
    }

    public CurrencyAccountDto cashIn(String id, Currency currency, BigDecimal amount){
        return executeTask(() -> userAccountService.cashIn(id, currency, amount));
    }

    public CurrencyAccountDto withdraw(String id, Currency currency, BigDecimal amount){
        return executeTask(() -> userAccountService.withdraw(id, currency, amount));
    }

    private  <T> T executeTask(Callable<T> callable) {
        Future<T> result = executorService.submit(callable);
        T upsertedConversion = null;
        try {
            upsertedConversion = result.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error occurred while processing request", e);
            Thread.currentThread().interrupt();
        }
        return upsertedConversion;
    }

}
