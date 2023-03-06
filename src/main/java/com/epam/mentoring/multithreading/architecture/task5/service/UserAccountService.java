package com.epam.mentoring.multithreading.architecture.task5.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

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
import com.epam.mentoring.multithreading.architecture.task5.util.ErrorCode;
import com.epam.mentoring.multithreading.architecture.task5.validator.UserAccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserAccountService {

    private final Lock lock = new ReentrantLock();

    private final Condition condition = lock.newCondition();

    private final Set<String> processingAccounts = new HashSet<>();

    private final UserAccountMapper userAccountMapper;

    private final UserAccountValidator userAccountValidator;

    private final UserAccountRepository userAccountRepository;

    private final ConversionService conversionService;

    public UserAccountDto createUserAccount(List<CurrencyAccountDto> currencyAccountDtos) {
        if (currencyAccountDtos.isEmpty()) {
            throw exception("Currencies list must not be empty");
        }
        final List<CurrencyAccount> currencyAccounts = currencyAccountDtos.stream()
                .peek(userAccountValidator::validateCurrencyAccount)
                .map(userAccountMapper::toCurrencyAccount)
                .collect(Collectors.toList());
        UserAccount userAccount = new UserAccount(UUID.randomUUID().toString(), currencyAccounts);
        userAccount = userAccountRepository.upsertUserAccount(userAccount);
        return userAccountMapper.toUserAccountDto(userAccount);
    }

    public CurrencyAccountDto cashIn(String id, Currency currency, BigDecimal amount) {
        CurrencyAccountDto currencyAccountDto = null;
        lock.lock();
        try {
            while (processingAccounts.contains(id)) {
                condition.await();
            }
            processingAccounts.add(id);
            final UserAccount userAccount = userAccountRepository.findUserAccountById(id).orElseThrow(
                    () -> exception(ErrorCode.USER_ACCOUNT_NOT_FOUND,
                            String.format("User account with id %s not found", id)));
            final CurrencyAccount currencyAccount = findCurrency(userAccount, currency);
            final BigDecimal currentAmount = currencyAccount.getAmount();
            currencyAccount.setAmount(currentAmount.add(amount));
            userAccountRepository.upsertUserAccount(userAccount);
            currencyAccountDto = userAccountMapper.toCurrencyAccountDto(currencyAccount);
        } catch (InterruptedException e) {
            log.error("Thread {} was interrupted", Thread.currentThread().getName());
            Thread.currentThread().interrupt();
        } finally {
            processingAccounts.remove(id);
            condition.signal();
            lock.unlock();
        }
        return currencyAccountDto;
    }

    public CurrencyAccountDto withdraw(String id, Currency currency, BigDecimal withdraw) {
        CurrencyAccountDto currencyAccountDto = null;
        lock.lock();
        try {
            while (processingAccounts.contains(id)) {
                condition.await();
            }
            processingAccounts.add(id);
            final UserAccount userAccount = userAccountRepository.findUserAccountById(id).orElseThrow(
                    () -> exception(ErrorCode.USER_ACCOUNT_NOT_FOUND,
                            String.format("User account with id %s not found", id)));
            final CurrencyAccount currencyAccount = checkFundsSufficiency(userAccount, currency, withdraw);
            final BigDecimal currentAmount = currencyAccount.getAmount();
            currencyAccount.setAmount(currentAmount.subtract(withdraw));
            userAccountRepository.upsertUserAccount(userAccount);
            currencyAccountDto = userAccountMapper.toCurrencyAccountDto(currencyAccount);
        } catch (InterruptedException e) {
            log.error("Thread {} was interrupted", Thread.currentThread().getName());
            Thread.currentThread().interrupt();
        } finally {
            processingAccounts.remove(id);
            condition.signal();
            lock.unlock();
        }
        return currencyAccountDto;
    }

    public ExchangeResponseDto exchangeCurrency(String id, Currency base, Currency target, BigDecimal targetAmount) {
        ExchangeResponseDto exchangeResponseDto = null;
        lock.lock();
        try {
            while (processingAccounts.contains(id)) {
                condition.await();
            }
            processingAccounts.add(id);
            final UserAccount userAccount = userAccountRepository.findUserAccountById(id).orElseThrow(
                    () -> exception(ErrorCode.USER_ACCOUNT_NOT_FOUND,
                            String.format("User account with id %s not found", id)));
            final BigDecimal withdrawAmount = calculateCurrencyWithdrawAmount(base, targetAmount, target);
            checkFundsSufficiency(userAccount, base, withdrawAmount);
            exchange(userAccount, base, withdrawAmount, target, targetAmount);
            userAccountRepository.upsertUserAccount(userAccount);
            exchangeResponseDto = new ExchangeResponseDto(base, withdrawAmount, target, targetAmount);
        } catch (InterruptedException e) {
            log.error("Thread {} was interrupted", Thread.currentThread().getName());
            Thread.currentThread().interrupt();
        } finally {
            processingAccounts.remove(id);
            condition.signal();
            lock.unlock();
        }
        return exchangeResponseDto;
    }

    private BigDecimal calculateCurrencyWithdrawAmount(Currency base, BigDecimal targetAmount, Currency target) {
        final ConversionDto conversion = conversionService.findConversionByCurrencies(base, target);
        if (conversion.getBase() == base) {
            return targetAmount.divide(conversion.getBuyRate(), RoundingMode.HALF_UP);
        } else {
            return targetAmount.multiply(conversion.getSellRate());
        }
    }

    private CurrencyAccount checkFundsSufficiency(UserAccount userAccount, Currency currency,
            BigDecimal withdrawAmount) {
        return userAccount.getCurrencyAccounts().stream()
                .filter(currencyAccount -> currency == currencyAccount.getCurrency()
                        && currencyAccount.getAmount().compareTo(withdrawAmount) >= 0).findFirst()
                .orElseThrow(() -> exception("Insufficient funds for the operation"));
    }

    private void exchange(UserAccount userAccount, Currency base, BigDecimal withdrawAmount, Currency target,
            BigDecimal targetAmount) {
        for (CurrencyAccount account : userAccount.getCurrencyAccounts()) {
            if (account.getCurrency() == base) {
                final BigDecimal currentAmount = account.getAmount();
                account.setAmount(currentAmount.subtract(withdrawAmount));
            }
            if (account.getCurrency() == target) {
                final BigDecimal currentAmount = account.getAmount();
                account.setAmount(currentAmount.add(targetAmount));
            }
        }
    }

    private CurrencyAccount findCurrency(UserAccount userAccount, Currency currency) {
        return userAccount.getCurrencyAccounts().stream()
                .filter(account -> currency == account.getCurrency())
                .findFirst()
                .orElseThrow(() -> exception(ErrorCode.USER_ACCOUNT_NOT_FOUND,
                        String.format("Currency %s not associated with account %s", currency, userAccount.getId())));
    }

    private UserAccountException exception(String message) {
        log.error(message);
        return new UserAccountException(ErrorCode.USER_ACCOUNT_BAD_REQUEST, message);
    }

    private UserAccountException exception(String errorCode, String message) {
        log.error(message);
        return new UserAccountException(errorCode, message);
    }

}
