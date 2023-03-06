package com.epam.mentoring.multithreading.architecture.task5.validator;

import com.epam.mentoring.multithreading.architecture.task5.dto.ConversionDto;
import com.epam.mentoring.multithreading.architecture.task5.exception.ConversionException;
import com.epam.mentoring.multithreading.architecture.task5.util.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConversionValidator {

    public void validateConversion(ConversionDto conversionDto) {
        if (conversionDto.getBase() == null) {
            throw exception("Base currency must not be null");
        }
        if (conversionDto.getTarget() == null) {
            throw exception("Target currency must not be null");
        }
        if (conversionDto.getBuyRate() == null) {
            throw exception("Buy Rate must not be null");
        }
        if (conversionDto.getBuyRate().doubleValue() <= 0) {
            throw exception("Buy Rate must be a positive number");
        }
        if (conversionDto.getSellRate() == null) {
            throw exception("Sell Rate must not be null");
        }
        if (conversionDto.getSellRate().doubleValue() <= 0) {
            throw exception("Sell Rate must be a positive number");
        }
        if (conversionDto.getBase() == conversionDto.getTarget()){
            throw exception("currencies should not be the same ");
        }
    }

    private ConversionException exception(String message) {
        log.error(message);
        return new ConversionException(ErrorCode.CONVERSION_BAD_REQUEST, message);
    }

}
