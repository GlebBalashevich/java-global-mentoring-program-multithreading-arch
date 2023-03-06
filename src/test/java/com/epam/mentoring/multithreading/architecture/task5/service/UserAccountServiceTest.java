package com.epam.mentoring.multithreading.architecture.task5.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.epam.mentoring.multithreading.architecture.task5.dto.ConversionDto;
import com.epam.mentoring.multithreading.architecture.task5.dto.CurrencyAccountDto;
import com.epam.mentoring.multithreading.architecture.task5.dto.ExchangeResponseDto;
import com.epam.mentoring.multithreading.architecture.task5.dto.UserAccountDto;
import com.epam.mentoring.multithreading.architecture.task5.exception.UserAccountException;
import com.epam.mentoring.multithreading.architecture.task5.mapper.UserAccountMapper;
import com.epam.mentoring.multithreading.architecture.task5.model.Currency;
import com.epam.mentoring.multithreading.architecture.task5.model.CurrencyAccount;
import com.epam.mentoring.multithreading.architecture.task5.model.UserAccount;
import com.epam.mentoring.multithreading.architecture.task5.repository.UserAccountRepository;
import com.epam.mentoring.multithreading.architecture.task5.validator.UserAccountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    private UserAccountService userAccountService;

    @Mock
    private UserAccountValidator userAccountValidator;

    @Mock
    private UserAccountMapper userAccountMapper;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private ConversionService conversionService;

    @BeforeEach
    void init() {
        userAccountService = new UserAccountService(userAccountMapper, userAccountValidator, userAccountRepository,
                conversionService);
    }

    @Test
    void testCreateUserAccount() {
        String id = UUID.randomUUID().toString();
        CurrencyAccountDto currencyAccountDto = new CurrencyAccountDto(Currency.JPY, BigDecimal.valueOf(1000));
        CurrencyAccount currencyAccount = new CurrencyAccount(Currency.JPY, BigDecimal.valueOf(1000));
        UserAccount userAccount = new UserAccount(id, List.of(currencyAccount));
        UserAccountDto expected = new UserAccountDto(id, List.of(currencyAccountDto));

        doNothing().when(userAccountValidator).validateCurrencyAccount(currencyAccountDto);
        when(userAccountMapper.toCurrencyAccount(currencyAccountDto)).thenReturn(currencyAccount);
        when(userAccountRepository.upsertUserAccount(any())).thenReturn(userAccount);
        when(userAccountMapper.toUserAccountDto(userAccount)).thenReturn(expected);

        UserAccountDto actual = userAccountService.createUserAccount(List.of(currencyAccountDto));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testCreateUser_EmptyCurrencyAccounts() {
        List<CurrencyAccountDto> emptyList = Collections.emptyList();
        assertThatThrownBy(() -> userAccountService.createUserAccount(emptyList)).isInstanceOf(
                UserAccountException.class);
    }

    @Test
    void testCreateUser_InvalidCurrencyAccount() {
        CurrencyAccountDto currencyAccountDto = new CurrencyAccountDto(Currency.JPY, null);
        List<CurrencyAccountDto> currencyAccountDtos = List.of(currencyAccountDto);
        doThrow(UserAccountException.class).when(userAccountValidator).validateCurrencyAccount(any());

        assertThatThrownBy(() -> userAccountService.createUserAccount(currencyAccountDtos)).isInstanceOf(
                UserAccountException.class);
    }

    @Test
    void testCashIn() {
        String id = UUID.randomUUID().toString();
        CurrencyAccount currencyAccount = new CurrencyAccount(Currency.JPY, BigDecimal.valueOf(1000));
        UserAccount userAccount = new UserAccount(id, List.of(currencyAccount));
        CurrencyAccountDto expected = new CurrencyAccountDto(Currency.JPY, BigDecimal.valueOf(1500));

        when(userAccountRepository.findUserAccountById(id)).thenReturn(Optional.of(userAccount));
        when(userAccountRepository.upsertUserAccount(any())).thenReturn(userAccount);
        currencyAccount.setAmount(BigDecimal.valueOf(1500));
        when(userAccountMapper.toCurrencyAccountDto(currencyAccount)).thenReturn(expected);

        CurrencyAccountDto actual = userAccountService.cashIn(id, Currency.JPY, BigDecimal.valueOf(500));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testCashIn_NoCurrency() {
        String id = UUID.randomUUID().toString();
        BigDecimal cashAmount = BigDecimal.valueOf(1000);
        CurrencyAccount currencyAccount = new CurrencyAccount(Currency.JPY, BigDecimal.valueOf(1000));
        UserAccount userAccount = new UserAccount(id, List.of(currencyAccount));

        when(userAccountRepository.findUserAccountById(id)).thenReturn(Optional.of(userAccount));

        assertThatThrownBy(() -> userAccountService.cashIn(id, Currency.EUR, cashAmount)).isInstanceOf(
                UserAccountException.class);
    }

    @Test
    void testWithdraw() {
        String id = UUID.randomUUID().toString();
        CurrencyAccount currencyAccount = new CurrencyAccount(Currency.JPY, BigDecimal.valueOf(1000));
        UserAccount userAccount = new UserAccount(id, List.of(currencyAccount));
        CurrencyAccountDto expected = new CurrencyAccountDto(Currency.JPY, BigDecimal.valueOf(500));

        when(userAccountRepository.findUserAccountById(id)).thenReturn(Optional.of(userAccount));
        when(userAccountRepository.upsertUserAccount(any())).thenReturn(userAccount);
        currencyAccount.setAmount(BigDecimal.valueOf(500));
        when(userAccountMapper.toCurrencyAccountDto(currencyAccount)).thenReturn(expected);

        CurrencyAccountDto actual = userAccountService.withdraw(id, Currency.JPY, BigDecimal.valueOf(500));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testWithdraw_NoSufficientAmount() {
        String id = UUID.randomUUID().toString();
        BigDecimal withdrawAmount = BigDecimal.valueOf(2000);
        CurrencyAccount currencyAccount = new CurrencyAccount(Currency.JPY, BigDecimal.valueOf(1000));
        UserAccount userAccount = new UserAccount(id, List.of(currencyAccount));

        when(userAccountRepository.findUserAccountById(id)).thenReturn(Optional.of(userAccount));

        assertThatThrownBy(() -> userAccountService.withdraw(id, Currency.EUR, withdrawAmount)).isInstanceOf(
                UserAccountException.class);
    }

    @Test
    void testExchangeThroughBuy() {
        String id = UUID.randomUUID().toString();
        CurrencyAccount usdAccount = new CurrencyAccount(Currency.USD, BigDecimal.valueOf(1000));
        CurrencyAccount eurAccount = new CurrencyAccount(Currency.EUR, BigDecimal.valueOf(1000));
        UserAccount userAccount = new UserAccount(id, List.of(usdAccount, eurAccount));
        ConversionDto conversionDto = new ConversionDto(Currency.EUR, Currency.USD, BigDecimal.valueOf(1.50),
                BigDecimal.valueOf(1.2));

        when(userAccountRepository.findUserAccountById(id)).thenReturn(Optional.of(userAccount));
        when(conversionService.findConversionByCurrencies(Currency.EUR, Currency.USD)).thenReturn(conversionDto);
        when(userAccountRepository.upsertUserAccount(any())).thenReturn(userAccount);

        ExchangeResponseDto actual = userAccountService.exchangeCurrency(id, Currency.EUR, Currency.USD,
                BigDecimal.valueOf(120));

        assertThat(actual.getBase()).isEqualTo(Currency.EUR);
        assertThat(actual.getTarget()).isEqualTo(Currency.USD);
        assertThat(actual.getTargetAmount()).isEqualTo(BigDecimal.valueOf(120));
        assertThat(actual.getWithdrawAmount()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void testExchangeThroughSell() {
        String id = UUID.randomUUID().toString();
        CurrencyAccount usdAccount = new CurrencyAccount(Currency.USD, BigDecimal.valueOf(1000));
        CurrencyAccount eurAccount = new CurrencyAccount(Currency.EUR, BigDecimal.valueOf(1000));
        UserAccount userAccount = new UserAccount(id, List.of(usdAccount, eurAccount));
        ConversionDto conversionDto = new ConversionDto(Currency.EUR, Currency.USD, BigDecimal.valueOf(1.5),
                BigDecimal.valueOf(1.2));

        when(userAccountRepository.findUserAccountById(id)).thenReturn(Optional.of(userAccount));
        when(conversionService.findConversionByCurrencies(Currency.USD, Currency.EUR)).thenReturn(conversionDto);
        when(userAccountRepository.upsertUserAccount(any())).thenReturn(userAccount);

        ExchangeResponseDto actual = userAccountService.exchangeCurrency(id, Currency.USD, Currency.EUR,
                BigDecimal.valueOf(100));

        assertThat(actual.getBase()).isEqualTo(Currency.USD);
        assertThat(actual.getTarget()).isEqualTo(Currency.EUR);
        assertThat(actual.getTargetAmount()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(actual.getWithdrawAmount()).isEqualTo(BigDecimal.valueOf(150.0));
    }

    @Test
    void testExchangeInsufficientWithdrawAmount() {
        String id = UUID.randomUUID().toString();
        BigDecimal exchangeAmount = BigDecimal.valueOf(10000);
        CurrencyAccount usdAccount = new CurrencyAccount(Currency.USD, BigDecimal.valueOf(1000));
        CurrencyAccount eurAccount = new CurrencyAccount(Currency.EUR, BigDecimal.valueOf(1000));
        UserAccount userAccount = new UserAccount(id, List.of(usdAccount, eurAccount));
        ConversionDto conversionDto = new ConversionDto(Currency.EUR, Currency.USD, BigDecimal.valueOf(1.5),
                BigDecimal.valueOf(1.2));

        when(userAccountRepository.findUserAccountById(id)).thenReturn(Optional.of(userAccount));
        when(conversionService.findConversionByCurrencies(Currency.USD, Currency.EUR)).thenReturn(conversionDto);

        assertThatThrownBy(() -> userAccountService.exchangeCurrency(id, Currency.USD, Currency.EUR, exchangeAmount))
                .isInstanceOf(UserAccountException.class);
    }

}
