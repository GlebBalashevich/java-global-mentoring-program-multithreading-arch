package com.epam.mentoring.multithreading.architecture.task5.repository;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

import com.epam.mentoring.multithreading.architecture.task5.exception.ConversionException;
import com.epam.mentoring.multithreading.architecture.task5.model.Conversion;
import com.epam.mentoring.multithreading.architecture.task5.model.Currency;
import com.epam.mentoring.multithreading.architecture.task5.util.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ConversionRepository {

    private static final String ROOT_DIRECTORY_PATH = "/conversions/";

    private static final String FILE_EXTENSION = ".json";

    private final ObjectMapper objectMapper;

    public Optional<Conversion> findConversionByCurrencies(Currency base, Currency target) {
        final File rootDirectory = getRootDirectory();
        final File[] files = findConversionFiles(rootDirectory, base, target);
        if (files == null || files.length == 0) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(files[0], Conversion.class));
        } catch (IOException e) {
            throw exception(ErrorCode.CONVERSION_CORRUPTED_DATA,
                    String.format("Conversion's data for base %s, target %s corrupted", base, target), e);
        }
    }

    public Conversion upsertConversion(Conversion conversion) {
        final File rootDirectory = getRootDirectory();
        final String fileName = "/" + conversion.getBase() + conversion.getTarget() + FILE_EXTENSION;
        final File file = new File(rootDirectory.getAbsolutePath() + fileName);
        try {
            if (file.createNewFile()) {
                log.info("A new file was created for storing conversion for currencies {} {}", conversion.getBase(),
                        conversion.getTarget());
            }
            objectMapper.writeValue(file, conversion);
            return conversion;
        } catch (IOException e) {
            throw exception(ErrorCode.CONVERSION_WRITE_ERROR, "Error occurred during conversion storing", e);
        }
    }

    private File[] findConversionFiles(File rootDirectory, Currency base, Currency target) {
        return rootDirectory.listFiles(
                f -> f.getName().equals(base.name() + target.name() + FILE_EXTENSION) || f.getName()
                        .equals(target.name() + base.name() + FILE_EXTENSION));
    }

    private File getRootDirectory() {
        try {
            return new File(Objects.requireNonNull(getClass().getResource(ROOT_DIRECTORY_PATH)).toURI());
        } catch (URISyntaxException e) {
            throw exception(ErrorCode.CONVERSION_DIRECTORY_NOT_FOUND, "Directory with conversions doesn't exists", e);
        }
    }

    private ConversionException exception(String errorCode, String message, Throwable cause) {
        log.error(message, cause);
        return new ConversionException(errorCode, message, cause);
    }

    private ConversionException exception(String errorCode, String message) {
        log.error(message);
        return new ConversionException(errorCode, message);
    }

}
