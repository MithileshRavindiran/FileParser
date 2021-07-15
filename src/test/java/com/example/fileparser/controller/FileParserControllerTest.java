package com.example.fileparser.controller;

import com.example.fileparser.domain.WordFileCount;
import com.example.fileparser.exception.FileParserException;
import com.example.fileparser.exception.GlobalExceptionHandler;
import com.example.fileparser.service.WordsExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;



public class FileParserControllerTest {

    private MockMvc mockMvc;

    @Mock
    WordsExtractor wordsExtractor;

    @InjectMocks
    FileParserController fileParserController;

    MockMultipartFile file;

    MockMultipartFile file2;

    ObjectMapper mapper;

    List<WordFileCount> wordFileCountList;

    private ExceptionHandlerExceptionResolver createExceptionResolver() {
        ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver(){
            @Override
            protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
                Method method = new ExceptionHandlerMethodResolver(GlobalExceptionHandler.class).resolveMethod(exception);
                return new ServletInvocableHandlerMethod(new GlobalExceptionHandler(), method);
            }
        };
        exceptionResolver.afterPropertiesSet();
        return exceptionResolver;
    }

    @BeforeEach
    public void setup() {

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(fileParserController)
                .setHandlerExceptionResolvers(createExceptionResolver())
                .build();
        wordFileCountList = createWordFileCounts();
        mapper = new ObjectMapper();
    }

    @Test
    public void whenFilesUploaded_ControllerProcesses_andThenRetrunTheWordFileCounts() throws Exception {
        when(wordsExtractor.extractWords(any())).thenReturn(wordFileCountList);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/upload")
                .file("file1.txt",  "some file".getBytes(StandardCharsets.UTF_8))
                .file("file2.txt", "random File".getBytes(StandardCharsets.UTF_8)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertEquals(mapper.writeValueAsString(wordFileCountList), mvcResult.getResponse().getContentAsString());
    }


    @Test
    public void whenFilesUploaded_ControllerProcesses_andThenRetrunException() throws Exception {
        when(wordsExtractor.extractWords(any())).thenThrow(new FileParserException("Exception Occurred"));
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/upload")
                .file("file1.txt",  "some file".getBytes(StandardCharsets.UTF_8))
                .file("file2.txt", "random File".getBytes(StandardCharsets.UTF_8)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();


    }

    private List<WordFileCount> createWordFileCounts() {
        return Arrays.asList(
                createWordFileCount("file1.txt", "file2.txt", 2l, 3l, "Wayne"),
                createWordFileCount("file1.txt", "file2.txt", 2l, 2l, "Bruce")
        );
    }


    private WordFileCount createWordFileCount(String fileOneName, String fileTwoName, Long fileOneCount, Long fileTwoCount, String word) {
        Map<String, Long> fileWordsCount = new HashMap<>();
        fileWordsCount.put(fileOneName, fileOneCount);
        fileWordsCount.put(fileTwoName, fileTwoCount);
        return WordFileCount.builder().word(word).totalCount(fileOneCount+fileTwoCount).fileWordsCount(fileWordsCount).build();
    }


}
