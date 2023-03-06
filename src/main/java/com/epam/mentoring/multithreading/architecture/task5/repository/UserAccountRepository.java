package com.epam.mentoring.multithreading.architecture.task5.repository;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

import com.epam.mentoring.multithreading.architecture.task5.exception.UserAccountException;
import com.epam.mentoring.multithreading.architecture.task5.model.UserAccount;
import com.epam.mentoring.multithreading.architecture.task5.util.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserAccountRepository {

    private static final String ROOT_DIRECTORY_PATH = "/accounts/";

    private static final String FILE_EXTENSION = ".json";

    private final ObjectMapper objectMapper;

    public Optional<UserAccount> findUserAccountById(String id) {
        final File rootDirectory = getRootDirectory();
        final File[] files = rootDirectory.listFiles(f -> f.getName().equals(id + FILE_EXTENSION));
        if (files == null || files.length == 0) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(files[0], UserAccount.class));
        } catch (IOException e) {
            throw exception(ErrorCode.USER_ACCOUNT_CORRUPTED_DATA,
                    String.format("User account's data for id %s corrupted", id), e);
        }
    }

    public UserAccount upsertUserAccount(UserAccount userAccount) {
        final File rootDirectory = getRootDirectory();
        final String fileName = "/" + userAccount.getId() + FILE_EXTENSION;
        final File file = new File(rootDirectory.getAbsolutePath() + fileName);
        try {
            if (file.createNewFile()) {
                log.info("A new file was created for storing user account with id {}", userAccount.getId());
            }
            objectMapper.writeValue(file, userAccount);
            return userAccount;
        } catch (IOException e) {
            throw exception(ErrorCode.USER_ACCOUNT_WRITE_ERROR, "Error occurred during user account storing", e);
        }
    }

    private File getRootDirectory() {
        try {
            return new File(Objects.requireNonNull(getClass().getResource(ROOT_DIRECTORY_PATH)).toURI());
        } catch (URISyntaxException e) {
            throw exception(ErrorCode.USER_ACCOUNT_DIRECTORY_NOT_FOUND, "Directory with user accounts doesn't exists",
                    e);
        }
    }

    private UserAccountException exception(String errorCode, String message, Throwable cause) {
        log.error(message, cause);
        return new UserAccountException(errorCode, message, cause);
    }

}
