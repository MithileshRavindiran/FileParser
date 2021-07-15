package com.example.fileparser.service.parser;

import com.example.fileparser.exception.FileParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;

import static com.example.fileparser.util.TestHelperUtil.getMockFile;
import static com.example.fileparser.utils.FileProcessingConstants.EXCEPTION_IN_PROCESSING_TEXT_FILE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TextFileParserImplTest {

    @InjectMocks
    TextFileParserImpl textFileParser;

    MockMultipartFile file;

    @BeforeEach
    public void setup()  {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void whenTextFileUploaded_TextParserParses_ThenExtractRecords() throws FileParserException, IOException {
        file = getMockFile("file1.txt", "text/plain", this.getClass().getClassLoader()
                .getResourceAsStream("file1.txt"));
        List<String> wordRecords = textFileParser.parse(file);
        assertEquals(6, wordRecords.size());
        assertAll("Sample",
                () -> assertEquals(6, wordRecords.size()),
                () -> assertEquals("Bruce"+"_"+file.getOriginalFilename(), wordRecords.get(0)),
                () -> assertEquals("Wayne"+"_"+file.getOriginalFilename(), wordRecords.get(1)),
                () ->assertEquals("Jason"+"_"+file.getOriginalFilename(), wordRecords.get(2)),
                () ->assertEquals("Statham"+"_"+file.getOriginalFilename(), wordRecords.get(3)),
                () ->assertEquals("Justin"+"_"+file.getOriginalFilename(), wordRecords.get(4)),
                () ->assertEquals("Trudeau"+"_"+file.getOriginalFilename(), wordRecords.get(5)));
    }

    @Test
    public void whenWrongFormattedFileUploaded_TextParserParses_ThenThrowsFileParserException() throws IOException {
        Exception exception = assertThrows(FileParserException.class, () -> textFileParser.parse(null));
        assertTrue(exception.getMessage().contains(EXCEPTION_IN_PROCESSING_TEXT_FILE));
    }
}
