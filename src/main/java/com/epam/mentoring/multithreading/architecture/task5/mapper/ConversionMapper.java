package com.epam.mentoring.multithreading.architecture.task5.mapper;

import com.epam.mentoring.multithreading.architecture.task5.dto.ConversionDto;
import com.epam.mentoring.multithreading.architecture.task5.model.Conversion;
import org.mapstruct.Mapper;

@Mapper
public interface ConversionMapper {

    ConversionDto toConversionDto(Conversion conversion);

    Conversion toConversion(ConversionDto conversionDto);

}
