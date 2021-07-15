package com.example.fileparser.util;


import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;


public final class TestHelperUtil {



    public static MockMultipartFile getMockFile(String name, String contentType, InputStream resourceAsStream) throws IOException {
        return new MockMultipartFile(name, name, contentType,
                resourceAsStream);
    }
}
