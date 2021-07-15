package com.example.fileparser.service.parser;

import com.example.fileparser.exception.FileParserException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface FileParser {

    /***
     * Method to parse the Uploaded multipart file and get the transactions from the uploaded file
     * @param file of type MultipartFile {@link MultipartFile}
     * @return List of Words
     * @throws FileParserException
     */
   List<String> parse(MultipartFile file) throws FileParserException, IOException;
}
