package com.example.fileparser.service.parser;

import com.example.fileparser.exception.FileParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.fileparser.utils.FileProcessingConstants.EXCEPTION_IN_PROCESSING_TEXT_FILE;


@Service
@RequiredArgsConstructor
@Slf4j
public class TextFileParserImpl implements FileParser{

    public static final String REGEX_WHITESPACE = "\\s+";

    /**
     * Method to parse the uploaded files and Get the words from the uploaded files
     * @param file of type MultipartFile {@link MultipartFile}
     * @return list of words seperated by whitespace characters
     * @throws FileParserException
     */
    @Override
    public List<String> parse(MultipartFile file) throws FileParserException {

        try (Stream<String> stream = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)).lines()) {
            return stream.flatMap(line -> Arrays.stream(line.split(REGEX_WHITESPACE))).map(x ->  x + '_' + file.getOriginalFilename()).collect(Collectors.toList());
        } catch (Exception ex) {
            log.debug("Parsing of the file failed due to  {}", ex.getMessage());
            throw new FileParserException(EXCEPTION_IN_PROCESSING_TEXT_FILE ,ex);
        }

    }
}
