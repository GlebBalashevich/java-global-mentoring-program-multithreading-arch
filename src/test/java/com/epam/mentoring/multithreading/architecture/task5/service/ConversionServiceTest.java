package com.epam.mentoring.multithreading.architecture.task5.service;

import java.math.BigDecimal;
import java.util.Optional;

import com.epam.mentoring.multithreading.architecture.task5.dto.ConversionDto;
import com.epam.mentoring.multithreading.architecture.task5.exception.ConversionException;
import com.epam.mentoring.multithreading.architecture.task5.mapper.ConversionMapper;
import com.epam.mentoring.multithreading.architecture.task5.model.Conversion;
import com.epam.mentoring.multithreading.architecture.task5.model.Currency;
import com.epam.mentoring.multithreading.architecture.task5.repository.ConversionRepository;
import com.epam.mentoring.multithreading.architecture.task5.validator.ConversionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {

    private ConversionService conversionService;

    @Mock
    private ConversionRepository conversionRepository;

    @Mock
    private ConversionMapper conversionMapper;

    @Mock
    private ConversionValidator conversionValidator;

    @BeforeEach
    void init() {
        conversionService = new ConversionService(conversionMapper, conversionRepository, conversionValidator);
    }

    @Test
    void testFindConversionByCurrencies() {
        Conversion conversion = new Conversion(Currency.EUR, Currency.USD, BigDecimal.valueOf(1.2),
                BigDecimal.valueOf(1.5));
        ConversionDto expected = new ConversionDto(Currency.EUR, Currency.USD, BigDecimal.valueOf(1.2),
                BigDecimal.valueOf(1.5));

        when(conversionRepository.findConversionByCurrencies(Currency.EUR, Currency.USD)).thenReturn(
                Optional.of(conversion));
        when(conversionMapper.toConversionDto(conversion)).thenReturn(expected);

        ConversionDto actual = conversionService.findConversionByCurrencies(Currency.EUR, Currency.USD);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFindConversionByCurrencies_ConversionNotFound() {
        when(conversionRepository.findConversionByCurrencies(Currency.EUR, Currency.USD)).thenReturn(
                Optional.empty());

        assertThatThrownBy(() -> conversionService.findConversionByCurrencies(Currency.EUR, Currency.USD)).isInstanceOf(
                ConversionException.class);
    }

    @Test
    void testUpsertConversion() {
        Conversion conversion = new Conversion(Currency.EUR, Currency.USD, BigDecimal.valueOf(1.2),
                BigDecimal.valueOf(1.5));
        ConversionDto conversionDto = new ConversionDto(Currency.EUR, Currency.USD, BigDecimal.valueOf(1.2),
                BigDecimal.valueOf(1.5));

        doNothing().when(conversionValidator).validateConversion(conversionDto);
        when(conversionMapper.toConversion(conversionDto)).thenReturn(conversion);
        when(conversionRepository.upsertConversion(conversion)).thenReturn(conversion);
        when(conversionMapper.toConversionDto(conversion)).thenReturn(conversionDto);

        ConversionDto actual = conversionService.upsertConversion(conversionDto);

        assertThat(actual).isEqualTo(conversionDto);
    }
}

