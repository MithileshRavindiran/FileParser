package com.example.fileparser.controller;

import com.example.fileparser.domain.WordFileCount;
import com.example.fileparser.exception.FileParserException;
import com.example.fileparser.service.WordsExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileParserController {

    private final WordsExtractor wordsExtractor;

    @Operation(summary = "Word Count Generator",
            description = "Upload the File and retrieve the word counts from uploaded files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved list",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE , array = @ArraySchema(schema = @Schema(implementation = WordFileCount.class)))),
            @ApiResponse(responseCode = "400", description = "Wrong format File uploaded/Exception while processing files", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Exception",  content = @Content)
    })
    @PostMapping(path = "/v1/upload", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<WordFileCount> uploadStatementsForValidation(MultipartFile[] files) throws FileParserException, IOException {
        log.info("Processing the statements uploaded");
        return wordsExtractor.extractWords(files);
    }
}
