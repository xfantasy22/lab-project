package ru.itmo.service.xml;

import lombok.extern.slf4j.Slf4j;
import ru.itmo.exception.ValidateException;
import ru.itmo.model.Route;
import ru.itmo.model.Routes;
import ru.itmo.validator.EntityValidator;
import ru.itmo.validator.FileValidator;

import javax.xml.bind.JAXB;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class XmlParserImpl implements XmlParser {

    @Override
    public List<Route> readDataFromFile(String fileName) {
        try {
            return mapXmlStringToList(mapXmlFileToString(fileName));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void writeDataToFile(List<Route> routeList, String fileName) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(validateFileForWrite(new File(fileName)))) {
            Routes routes = Routes.builder().routes(routeList).build();
            JAXB.marshal(routes, fileOutputStream);
            log.info("File has been written: {}", fileName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private File validateFileForWrite(File file) throws IOException {
        try {
            return FileValidator.checkWriteProperty(file);
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                log.error(e.getMessage());
                return createXmlFile(file);
            }
            throw e;
        }
    }

    private String mapXmlFileToString(String fileName) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(FileValidator.checkReadProperty(new File(fileName)));
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        if (stringBuilder.length() == 0) {
            log.warn("File is empty");
        }
        scanner.close();
        return stringBuilder.toString();
    }

    private List<Route> mapXmlStringToList(String xmlString) {
        Routes routes = JAXB.unmarshal(new StringReader(xmlString), Routes.class);
        if (routes == null) {
            return Collections.emptyList();
        }
        return EntityValidator.checkRoutes(routes.getRoutes());
    }

    private File createXmlFile(File file) {
        String absPath = file.getAbsolutePath();
        try {
            return FileValidator.checkFileType(file);
        } catch (ValidateException e) {
            String fileFormat = ".xml";
            if (absPath.contains(".")) {
                return new File(absPath.substring(0, absPath.lastIndexOf(".")) + fileFormat);
            }
            return new File(absPath + fileFormat);
        }
    }
}
