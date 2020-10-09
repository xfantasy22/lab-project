package ru.itmo.validator;

import ru.itmo.exception.ValidateException;

import java.io.File;
import java.io.FileNotFoundException;

public class FileValidator {

    private static void checkExist(File file) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(String.format("File does not exist or wrong file format: %s", file.getName()));
        }
        if (!file.isFile()) {
            throw new ValidateException("This is not file!");
        }
    }

    public static File checkReadProperty(File file) throws FileNotFoundException {
        checkExist(file);

        if (!file.canRead()) {
            throw new ValidateException("Can't read file");
        }
        return file;
    }
}
