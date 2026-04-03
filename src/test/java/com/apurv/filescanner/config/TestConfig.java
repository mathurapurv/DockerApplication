package com.apurv.filescanner.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@TestConfiguration
public class TestConfig {
    
    @Bean
    public FileScannerProperties fileScannerProperties() {
        FileScannerProperties properties = new FileScannerProperties();
        Path testDir = Paths.get("src/test/resources/test-watch-dir");
        properties.setWatchDirectories(List.of(testDir.toAbsolutePath().toString()));
        properties.setArchiveSubfolder("test-archive");
        return properties;
    }
}
