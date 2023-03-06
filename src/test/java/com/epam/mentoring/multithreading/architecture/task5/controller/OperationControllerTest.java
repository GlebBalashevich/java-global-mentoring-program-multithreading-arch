package com.epam.mentoring.multithreading.architecture.task5.controller;

import java.io.File;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.epam.mentoring.multithreading.architecture.task5.dto.ConversionDto;
import com.epam.mentoring.multithreading.architecture.task5.dto.CurrencyAccountDto;
import com.epam.mentoring.multithreading.architecture.task5.dto.ExchangeResponseDto;
import com.epam.mentoring.multithreading.architecture.task5.dto.UserAccountDto;
import com.epam.mentoring.multithreading.architecture.task5.mapper.ConversionMapper;
import com.epam.mentoring.multithreading.architecture.task5.mapper.ConversionMapperImpl;
import com.epam.mentoring.multithreading.architecture.task5.mapper.UserAccountMapper;
import com.epam.mentoring.multithreading.architecture.task5.mapper.UserAccountMapperImpl;
import com.epam.mentoring.multithreading.architecture.task5.model.Conversion;
import com.epam.mentoring.multithreading.architecture.task5.model.Currency;
import com.epam.mentoring.multithreading.architecture.task5.model.CurrencyAccount;
import com.epam.mentoring.multithreading.architecture.task5.model.UserAccount;
import com.epam.mentoring.multithreading.architecture.task5.repository.ConversionRepository;
import com.epam.mentoring.multithreading.architecture.task5.repository.UserAccountRepository;
import com.epam.mentoring.multithreading.architecture.task5.service.ConversionService;
import com.epam.mentoring.multithreading.architecture.task5.service.UserAccountService;
import com.epam.mentoring.multithreading.architecture.task5.validator.ConversionValidator;
import com.epam.mentoring.multithreading.architecture.task5.validator.UserAccountValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperationControllerTest {

    private OperationController operationController;

    private UserAccountRepository userAccountRepository;

    private ConversionRepository conversionRepository;

    @BeforeEach
    void init() {
        ObjectMapper objectMapper = new ObjectMapper();
        conversionRepository = new ConversionRepository(objectMapper);
        userAccountRepository = new UserAccountRepository(objectMapper);
        UserAccountMapper userAccountMapper = new UserAccountMapperImpl();
        UserAccountValidator userAccountValidator = new UserAccountValidator();
        ConversionMapper conversionMapper = new ConversionMapperImpl();
        ConversionValidator conversionValidator = new ConversionValidator();
        ConversionService conversionService = new ConversionService(conversionMapper, conversionRepository,
                conversionValidator);
        UserAccountService userAccountService = new UserAccountService(userAccountMapper, userAccountValidator,
                userAccountRepository, conversionService);
        operationController = new OperationController(conversionService, userAccountService);
    }

    @Test
    void testUpsertConversionCreateNew() throws URISyntaxException {
        ConversionDto conversionDto = new ConversionDto(Currency.BYN, Currency.JPY, BigDecimal.valueOf(1.0),
                BigDecimal.valueOf(1.0));

        ConversionDto actual = operationController.upsertConversion(conversionDto);
        File file = new File(Objects.requireNonNull(getClass().getResource("/conversions/BYNJPY.json")).toURI());

        assertThat(actual).isEqualTo(conversionDto);
        assertTrue(file.exists());
        file.deleteOnExit();
    }

    @Test
    void testUpsertConversionUpdateExisting() throws URISyntaxException {
        Conversion initialConversion = new Conversion(Currency.BYN, Currency.JPY, BigDecimal.valueOf(2.0),
                BigDecimal.valueOf(2.0));
        ConversionDto conversionDto = new ConversionDto(Currency.BYN, Currency.JPY, BigDecimal.valueOf(1.0),
                BigDecimal.valueOf(1.0));

        conversionRepository.upsertConversion(initialConversion);

        ConversionDto actual = operationController.upsertConversion(conversionDto);
        File file = new File(Objects.requireNonNull(getClass().getResource("/conversions/BYNJPY.json")).toURI());

        assertThat(actual).isEqualTo(conversionDto);
        assertTrue(file.exists());
        file.deleteOnExit();
    }

    @Test
    void testFindConversionByCurrencies() throws URISyntaxException {
        Conversion conversion = new Conversion(Currency.BYN, Currency.JPY, BigDecimal.valueOf(5.0),
                BigDecimal.valueOf(5.0));

        conversionRepository.upsertConversion(conversion);

        ConversionDto actual = operationController.findConversionByCurrencies(Currency.BYN, Currency.JPY);
        File file = new File(Objects.requireNonNull(getClass().getResource("/conversions/BYNJPY.json")).toURI());

        assertThat(actual.getBase()).isEqualTo(conversion.getBase());
        assertThat(actual.getTarget()).isEqualTo(conversion.getTarget());
        assertThat(actual.getSellRate()).isEqualTo(conversion.getSellRate());
        assertThat(actual.getBuyRate()).isEqualTo(conversion.getBuyRate());
        assertTrue(file.exists());
        file.deleteOnExit();
    }

    @Test
    void testExchangeCurrency() throws URISyntaxException {
        String id = UUID.randomUUID().toString();
        Conversion conversion = new Conversion(Currency.BYN, Currency.JPY, BigDecimal.valueOf(1.0),
                BigDecimal.valueOf(1.0));
        CurrencyAccount bynAccount = new CurrencyAccount(Currency.BYN, BigDecimal.valueOf(1000));
        CurrencyAccount jpyAccount = new CurrencyAccount(Currency.JPY, BigDecimal.valueOf(10000));
        UserAccount userAccount = new UserAccount(id, List.of(bynAccount, jpyAccount));
        conversionRepository.upsertConversion(conversion);
        userAccountRepository.upsertUserAccount(userAccount);

        ExchangeResponseDto actual = operationController.exchangeCurrency(id, Currency.BYN, Currency.JPY,
                BigDecimal.valueOf(500));
        File conversionFile = new File(
                Objects.requireNonNull(getClass().getResource("/conversions/BYNJPY.json")).toURI());
        File accountFile = new File(
                Objects.requireNonNull(getClass().getResource("/accounts/" + id + ".json")).toURI());

        assertThat(actual.getBase()).isEqualTo(Currency.BYN);
        assertThat(actual.getTarget()).isEqualTo(Currency.JPY);
        assertThat(actual.getWithdrawAmount()).isEqualTo(BigDecimal.valueOf(500));
        assertThat(actual.getTargetAmount()).isEqualTo(BigDecimal.valueOf(500));
        assertTrue(conversionFile.exists());
        assertTrue(accountFile.exists());
        conversionFile.deleteOnExit();
        accountFile.deleteOnExit();
    }

    @Test
    void testCreateUserAccount() throws URISyntaxException {
        CurrencyAccountDto bynAccount = new CurrencyAccountDto(Currency.BYN, BigDecimal.valueOf(1000));
        CurrencyAccountDto jpyAccount = new CurrencyAccountDto(Currency.JPY, BigDecimal.valueOf(10000));

        UserAccountDto actual = operationController.createUserAccount(List.of(bynAccount, jpyAccount));
        File accountFile = new File(
                Objects.requireNonNull(getClass().getResource("/accounts/" + actual.getId() + ".json")).toURI());

        assertThat(actual.getCurrencyAccounts()).hasSize(2);
        assertTrue(accountFile.exists());
        accountFile.deleteOnExit();
    }

    @Test
    void testCashIn() throws URISyntaxException {
        String id = UUID.randomUUID().toString();
        CurrencyAccount bynAccount = new CurrencyAccount(Currency.BYN, BigDecimal.valueOf(1000));
        CurrencyAccount jpyAccount = new CurrencyAccount(Currency.JPY, BigDecimal.valueOf(10000));
        UserAccount userAccount = new UserAccount(id, List.of(bynAccount, jpyAccount));
        userAccountRepository.upsertUserAccount(userAccount);

        CurrencyAccountDto actual = operationController.cashIn(id, Currency.BYN, BigDecimal.valueOf(500));
        File accountFile = new File(
                Objects.requireNonNull(getClass().getResource("/accounts/" + id + ".json")).toURI());

        assertThat(actual.getAmount()).isEqualTo(BigDecimal.valueOf(1500));
        assertTrue(accountFile.exists());
        accountFile.deleteOnExit();
    }

    @Test
    void testWithdraw() throws URISyntaxException {
        String id = UUID.randomUUID().toString();
        CurrencyAccount bynAccount = new CurrencyAccount(Currency.BYN, BigDecimal.valueOf(1000));
        CurrencyAccount jpyAccount = new CurrencyAccount(Currency.JPY, BigDecimal.valueOf(10000));
        UserAccount userAccount = new UserAccount(id, List.of(bynAccount, jpyAccount));
        userAccountRepository.upsertUserAccount(userAccount);

        CurrencyAccountDto actual = operationController.withdraw(id, Currency.BYN, BigDecimal.valueOf(500));
        File accountFile = new File(
                Objects.requireNonNull(getClass().getResource("/accounts/" + id + ".json")).toURI());

        assertThat(actual.getAmount()).isEqualTo(BigDecimal.valueOf(500));
        assertTrue(accountFile.exists());
        accountFile.deleteOnExit();
    }

}
