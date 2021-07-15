package com.example.fileparser.service;

import com.example.fileparser.domain.WordFileCount;
import com.example.fileparser.exception.FileParserException;
import com.example.fileparser.service.parser.TextFileParserImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.fileparser.utils.FileProcessingConstants.*;


/**
 *
 * Uses to extract the  words from the uploaded  file
 */
@Service
@Slf4j
public class WordsExtractorImpl implements WordsExtractor {

    public static final String REGEX_UNDERSCORE = "_";
    private static ExecutorService service = Executors.newFixedThreadPool(2);

    @Autowired
    private TextFileParserImpl textFileParser;


    /***
     * Method the extract  the words form the MultipartFile
     * @param multipartFiles of type MultiPartFile {@link MultipartFile}
     * @return List of WordFileCount {@link WordFileCount}
     * @throws FileParserException
     */
    @Override
    public List<WordFileCount> extractWords(MultipartFile[] multipartFiles) throws FileParserException {

        if (multipartFiles == null || multipartFiles.length == 0) {
            throw new FileParserException(EXCEPTION_EMPTY_FILE);
        }
        List<CompletableFuture<List<String>>> completableFutureList = new ArrayList<>();
        List<WordFileCount> wordFileCountList = null;
        try {
            completableFutureList = Arrays.stream(multipartFiles)
                    .map(file -> CompletableFuture.supplyAsync(() -> extractFile(file), service)
                            .whenComplete(((msg,ex) -> {
                                if (ex != null) {
                                    log.debug(ex.getMessage());
                                }
                            })))
                    .collect(Collectors.toList());

            List<Map.Entry<String, Long>> listValues = completableFutureList.stream().map(CompletableFuture::join)
                    .map(x -> x.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())))
                    .flatMap(y -> y.entrySet().stream()).collect(Collectors.toList());
            //.collect(Collectors.toMap(Map.Entry::getKey,  Map.Entry::getValue, (x, y) -> x  + y));

            wordFileCountList = extractWordsAndCount(listValues);

            if (!wordFileCountList.isEmpty()) {
                Comparator<WordFileCount> wordFileTotalCountComparator = Comparator.comparing(WordFileCount::getTotalCount);
                Collections.sort(wordFileCountList, wordFileTotalCountComparator.reversed());
            }

            return wordFileCountList;
         }catch (CompletionException ex) {
             throw new FileParserException(EXCEPTION_IN_PROCESSING_TEXT_FILE + ":" + ex.getMessage(), ex);
        }

    }

    private List<WordFileCount> extractWordsAndCount(List<Map.Entry<String, Long>> listValues) {
        List<WordFileCount> wordFileCountList = new ArrayList<>();
        listValues.forEach(x -> {
            Optional<WordFileCount> matchingWordFileCount = wordFileCountList.stream().filter(word -> {
                        String[] parts = x.getKey().split(REGEX_UNDERSCORE);
                        return parts[0].equalsIgnoreCase(word.getWord());
                    }
            ).findFirst();
            createOrUpdateWordsList(wordFileCountList, x, matchingWordFileCount);
        });
        return wordFileCountList;
    }

    private void createOrUpdateWordsList(List<WordFileCount> wordFileCountList, Map.Entry<String, Long> x, Optional<WordFileCount> matchingWordFileCount) {
        if (matchingWordFileCount.isPresent()) {
            WordFileCount wordFileCount = matchingWordFileCount.get();
            String[] parts = x.getKey().split(REGEX_UNDERSCORE);
            wordFileCount.getFileWordsCount().put(parts[1], x.getValue());
            wordFileCount.setTotalCount(wordFileCount.getFileWordsCount().values().stream().reduce(0l, Long::sum));
        } else {
            String[] parts = x.getKey().split(REGEX_UNDERSCORE);
            WordFileCount wordFileCount = new WordFileCount();
            wordFileCount.setWord(parts[0]);
            wordFileCount.getFileWordsCount().put(parts[1], x.getValue());
            wordFileCount.setTotalCount(wordFileCount.getFileWordsCount().values().stream().reduce(0l, Long::sum));
            wordFileCountList.add(wordFileCount);
        }
    }


    private List<String> extractFile(MultipartFile multipartFile) throws FileParserException {
        String contentType = multipartFile.getContentType();
        log.info(EXTRACTING_WORDS_FROM_FILE);
        if (multipartFile.isEmpty()) {
            log.debug("File uploaded is not a empty", contentType);
            throw new FileParserException(EXCEPTION_EMPTY_FILE + ":" + multipartFile.getOriginalFilename());
        }
        if (!CONTENT_TYPE_APPLICATION_TXT.equalsIgnoreCase(contentType)) {
            log.debug("File uploaded is not a text file,File is of type {}", contentType);
            throw new FileParserException(EXCEPTION_UNSUPPORTED_FILE_TYPE + ":" + multipartFile.getOriginalFilename());
        }
        return textFileParser.parse(multipartFile);
    }


}
