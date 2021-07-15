package com.example.fileparser.service;

import com.example.fileparser.domain.WordFileCount;
import com.example.fileparser.exception.FileParserException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface WordsExtractor {

    /***
     * Method the extract  the transactions form the MultipartFile
     * @param multipartFiles of type MultiPartFile {@link MultipartFile}
     * @return List of Words
     * @throws FileParserException
     */
    List<WordFileCount> extractWords(MultipartFile[] multipartFiles) throws FileParserException, IOException;
}
