package com.example.fileparser.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Class holds the word staistics information from uploaded files with individual file count and total count
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordFileCount {

     private String word;

     private Map<String, Long> fileWordsCount = new HashMap<>();

     private long totalCount;


}
