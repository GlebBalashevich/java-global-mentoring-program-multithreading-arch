package com.epam.mentoring.multithreading.architecture.task5.validator;

import com.epam.mentoring.multithreading.architecture.task5.dto.CurrencyAccountDto;
import com.epam.mentoring.multithreading.architecture.task5.exception.ConversionException;
import com.epam.mentoring.multithreading.architecture.task5.util.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAccountValidator {

    public void validateCurrencyAccount(CurrencyAccountDto currencyAccountDto){
        if (currencyAccountDto.getCurrency() == null){
            throw exception("Currency must not be null");
        }
        if (currencyAccountDto.getAmount() == null){
            throw exception("Amount must not be null");
        }
        if (currencyAccountDto.getAmount().doubleValue() < 0){
            throw exception("Amount couldn't be a negative number");
        }
    }

    private ConversionException exception(String message) {
        log.error(message);
        return new ConversionException(ErrorCode.USER_ACCOUNT_BAD_REQUEST, message);
    }

}
