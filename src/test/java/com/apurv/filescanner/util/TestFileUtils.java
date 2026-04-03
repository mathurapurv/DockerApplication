package com.apurv.filescanner.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestFileUtils {
    
    public static void createTestFile(Path directory, String filename, String content) throws IOException {
        Path filePath = directory.resolve(filename);
        Files.writeString(filePath, content);
    }
    
    public static void cleanupTestDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                 .sorted((p1, p2) -> -p1.compareTo(p2)) // Delete files before directories
                 .forEach(path -> {
                     try {
                         Files.deleteIfExists(path);
                     } catch (IOException e) {
                         throw new RuntimeException("Failed to delete " + path, e);
                     }
                 });
        }
    }
    
    public static boolean fileExists(Path directory, String filename) {
        return Files.exists(directory.resolve(filename));
    }
}
