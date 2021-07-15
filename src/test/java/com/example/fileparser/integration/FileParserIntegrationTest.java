package com.example.fileparser.integration;

import com.example.fileparser.service.WordsExtractor;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;


import java.awt.*;

import static com.example.fileparser.util.TestHelperUtil.getMockFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FileParserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WordsExtractor wordsExtractor;

    @Autowired
    private WebApplicationContext webApplicationContext;



    @Test
    void whenFilesUploaded_ControllerProcess_providesWordFileCountList() throws Exception {
        String response = "[{\"word\":\"Justin\",\"fileWordsCount\":{\"file1.txt\":1},\"totalCount\":1},{\"word\":\"Jason\",\"fileWordsCount\":{\"file1.txt\":1},\"totalCount\":1},{\"word\":\"Wayne\",\"fileWordsCount\":{\"file1.txt\":1},\"totalCount\":1},{\"word\":\"Trudeau\",\"fileWordsCount\":{\"file1.txt\":1},\"totalCount\":1},{\"word\":\"Statham\",\"fileWordsCount\":{\"file1.txt\":1},\"totalCount\":1},{\"word\":\"Bruce\",\"fileWordsCount\":{\"file1.txt\":1},\"totalCount\":1}]";
        byte[] file1 = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("file1.txt"));
        MockMultipartFile multipartFile1 = new MockMultipartFile("files", "file1.txt", MediaType.TEXT_PLAIN_VALUE, file1);
        byte[] file2 = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("file1.txt"));
        MockMultipartFile multipartFile2 = new MockMultipartFile("files", "file1.txt", MediaType.TEXT_PLAIN_VALUE, file2);
       MvcResult mvcResult =  mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/upload")
        .file(multipartFile1).file(multipartFile2))
               .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        assertEquals(response, mvcResult.getResponse().getContentAsString());

    }






}
