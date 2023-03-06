package com.epam.mentoring.multithreading.architecture.task5.mapper;

import com.epam.mentoring.multithreading.architecture.task5.dto.CurrencyAccountDto;
import com.epam.mentoring.multithreading.architecture.task5.dto.UserAccountDto;
import com.epam.mentoring.multithreading.architecture.task5.model.CurrencyAccount;
import com.epam.mentoring.multithreading.architecture.task5.model.UserAccount;
import org.mapstruct.Mapper;

@Mapper
public interface UserAccountMapper {

    CurrencyAccountDto toCurrencyAccountDto(CurrencyAccount currencyAccount);

    CurrencyAccount toCurrencyAccount(CurrencyAccountDto currencyAccountDto);

    UserAccountDto toUserAccountDto(UserAccount userAccount);
}
