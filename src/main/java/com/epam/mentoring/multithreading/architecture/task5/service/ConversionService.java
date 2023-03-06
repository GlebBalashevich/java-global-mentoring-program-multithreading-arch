package com.epam.mentoring.multithreading.architecture.task5.service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.epam.mentoring.multithreading.architecture.task5.dto.ConversionDto;
import com.epam.mentoring.multithreading.architecture.task5.exception.ConversionException;
import com.epam.mentoring.multithreading.architecture.task5.mapper.ConversionMapper;
import com.epam.mentoring.multithreading.architecture.task5.model.Conversion;
import com.epam.mentoring.multithreading.architecture.task5.model.Currency;
import com.epam.mentoring.multithreading.architecture.task5.repository.ConversionRepository;
import com.epam.mentoring.multithreading.architecture.task5.util.ErrorCode;
import com.epam.mentoring.multithreading.architecture.task5.validator.ConversionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ConversionService {

    private final Lock lock = new ReentrantLock();

    private final ConversionMapper conversionMapper;

    private final ConversionRepository conversionRepository;

    private final ConversionValidator conversionValidator;

    public ConversionDto findConversionByCurrencies(Currency base, Currency target) {
        if (base == null || target == null) {
            throw exception("Currencies must not be null");
        }
        return conversionRepository.findConversionByCurrencies(base, target)
                .map(conversionMapper::toConversionDto)
                .orElseThrow(() -> exception(ErrorCode.CONVERSION_NOT_FOUND,
                        String.format("Conversion for base %s, target %s not found", base, target)));
    }

    public ConversionDto upsertConversion(ConversionDto conversionDto) {
        conversionValidator.validateConversion(conversionDto);
        Conversion conversion = conversionMapper.toConversion(conversionDto);
        lock.lock();
        try {
            conversion = conversionRepository.upsertConversion(conversion);
        } finally {
            lock.unlock();
        }
        return conversionMapper.toConversionDto(conversion);
    }

    private ConversionException exception(String message) {
        log.error(message);
        return new ConversionException(ErrorCode.CONVERSION_BAD_REQUEST, message);
    }

    private ConversionException exception(String errorCode, String message) {
        log.error(message);
        return new ConversionException(errorCode, message);
    }

}
