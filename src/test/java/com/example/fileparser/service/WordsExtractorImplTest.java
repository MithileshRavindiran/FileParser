package com.example.fileparser.service;

import com.example.fileparser.domain.WordFileCount;
import com.example.fileparser.exception.FileParserException;
import com.example.fileparser.service.parser.TextFileParserImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.example.fileparser.util.TestHelperUtil.getMockFile;
import static com.example.fileparser.utils.FileProcessingConstants.EXCEPTION_IN_PROCESSING_TEXT_FILE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class WordsExtractorImplTest {

    @InjectMocks
    WordsExtractorImpl wordsExtractor;

    @Mock
    TextFileParserImpl textFileParser;

    MockMultipartFile file1;

    MockMultipartFile file2;

    MultipartFile[] multipartFiles;

    @BeforeEach
    public void  setup() throws IOException {
        MockitoAnnotations.openMocks(this);
        file1 = getMockFile("file1.txt", "text/plain", this.getClass().getClassLoader()
                .getResourceAsStream("file1.txt"));
        file2 = getMockFile("file2.txt", "text/plain", this.getClass().getClassLoader()
                .getResourceAsStream("file2.txt"));
        multipartFiles = new MultipartFile[2];
        multipartFiles[0] = file1;
        multipartFiles[1] = file2;
    }

    @Test
    public void whenFileUploaded_thenWordsExtractedSuccesfully() throws FileParserException {

        when(textFileParser.parse(file1)).thenReturn(Arrays.asList("John_file1.txt", "Wayne_file1.txt", "Justin_file1.txt", "Trudeau_file1.txt"));
        when(textFileParser.parse(file2)).thenReturn(Arrays.asList("Justin_file2.txt", "Bieber_file2.txt", "Jason_file2.txt", "Statham_file2.txt"));
        List<WordFileCount>  resultTransactionRecords = wordsExtractor.extractWords(multipartFiles);
        assertAll("Validating the Response",
                () -> assertNotNull(resultTransactionRecords),
                () -> assertEquals(7, resultTransactionRecords.size()),
                () -> assertEquals("Justin", resultTransactionRecords.get(0).getWord()),
                () -> assertEquals(2, resultTransactionRecords.get(0).getTotalCount()));
    }


    @Test
    public void whenNoFileUploaded_thenExtractorThrowsFileParserException() throws FileParserException {
        multipartFiles = new MultipartFile[1];
        when(textFileParser.parse(file1)).thenThrow(new FileParserException(EXCEPTION_IN_PROCESSING_TEXT_FILE));
        Exception exception = assertThrows(FileParserException.class, () -> wordsExtractor.extractWords(multipartFiles));
        assertTrue(exception.getMessage().contains(EXCEPTION_IN_PROCESSING_TEXT_FILE));
    }

    @Test
    public void whenEmptyFileUploaded_thenExtractorThrowsFileParserExceptionOnThatFileAndStopsTheProcess() throws IOException {
        file1 = getMockFile("file3.txt", "text/plain", this.getClass().getClassLoader()
                .getResourceAsStream("file3.txt"));
        multipartFiles = new MultipartFile[2];
        multipartFiles[0] = file1;
        multipartFiles[1] = file2;
        when(textFileParser.parse(any())).thenReturn(Arrays.asList("John_file1.txt", "Wayne_file1.txt", "Justin_file1.txt", "Trudeau_file1.txt"));
        Exception exception = assertThrows(FileParserException.class, () -> wordsExtractor.extractWords(multipartFiles));
        assertTrue(exception.getMessage().contains(EXCEPTION_IN_PROCESSING_TEXT_FILE));
    }

    @Test
    public void whenWrongFileFormatUploaded_thenExtractorThrowsFileParserExceptionOnThatFileAndStopsTheProcess() throws IOException {
        file1 = getMockFile("file3.json", "application/json", this.getClass().getClassLoader()
                .getResourceAsStream("myrecord_wrongFormat.json"));
        multipartFiles = new MultipartFile[2];
        multipartFiles[0] = file1;
        multipartFiles[1] = file2;
        when(textFileParser.parse(any())).thenReturn(Arrays.asList("John_file1.txt", "Wayne_file1.txt", "Justin_file1.txt", "Trudeau_file1.txt"));
        Exception exception = assertThrows(FileParserException.class, () -> wordsExtractor.extractWords(multipartFiles));
        assertTrue(exception.getMessage().contains(EXCEPTION_IN_PROCESSING_TEXT_FILE));
    }




}
