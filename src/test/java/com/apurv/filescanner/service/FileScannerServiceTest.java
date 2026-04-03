package com.apurv.filescanner.service;

import com.apurv.filescanner.config.FileScannerProperties;
import com.apurv.filescanner.util.TestFileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileScannerServiceTest {

    @Mock
    private FileScannerProperties properties;

    @InjectMocks
    private FileScannerService fileScannerService;

    private Path testDir;
    private Path archiveDir;
    private static final String TEST_FILE = "test.txt";
    private static final String TEST_CONTENT = "Hello, World!";

    @BeforeEach
    void setUp() throws IOException {
        testDir = Paths.get("src/test/resources/test-watch-dir").toAbsolutePath();
        archiveDir = testDir.resolve("test-archive");
        
        // Setup test directories
        Files.createDirectories(testDir);
        Files.createDirectories(archiveDir);
        
        // Mock properties
        when(properties.getWatchDirectories()).thenReturn(List.of(testDir.toString()));
        when(properties.getArchiveSubfolder()).thenReturn("test-archive");
        
        // Initialize the service
        fileScannerService = new FileScannerService(properties);
        fileScannerService.init();
    }

    @AfterEach
    void tearDown() throws IOException {
        // Cleanup test directories
        TestFileUtils.cleanupTestDirectory(testDir);
        // Ensure service is properly shut down
        fileScannerService.cleanup();
    }

    @Test
    void whenNewFileCreated_thenItIsProcessedAndMovedToArchive() throws Exception {
        // Given
        Path testFile = testDir.resolve(TEST_FILE);
        
        // When
        TestFileUtils.createTestFile(testDir, TEST_FILE, TEST_CONTENT);
        
        // Small delay to allow file processing
        Thread.sleep(1000);
        
        // Then
        List<String> processedFiles = fileScannerService.getProcessedFiles();
        assertFalse(processedFiles.isEmpty(), "Processed files list should not be empty");
        assertEquals(TEST_CONTENT, processedFiles.get(0), "File content should match");
        
        // Verify file was moved to archive
        assertFalse(Files.exists(testFile), "Original file should be moved");
        assertTrue(Files.exists(archiveDir.resolve(TEST_FILE)), "File should exist in archive");
    }

    @Test
    void whenServiceInitialized_thenArchiveDirectoryIsCreated() {
        // Then
        assertTrue(Files.exists(archiveDir), "Archive directory should be created");
        assertTrue(Files.isDirectory(archiveDir), "Archive should be a directory");
    }

    // @Test
    void whenMultipleFilesCreated_thenAllAreProcessed() throws Exception {
        // Given
        String[] fileNames = {"file1.txt", "file2.txt", "file3.txt"};
        
        // When
        for (String fileName : fileNames) {
            TestFileUtils.createTestFile(testDir, fileName, "Content of " + fileName);
            Thread.sleep(100); // Small delay between file creations
        }
        
        // Small delay to allow all files to be processed
        Thread.sleep(1000);
        
        // Then
        List<String> processedFiles = fileScannerService.getProcessedFiles();
        assertEquals(fileNames.length, processedFiles.size(), "All files should be processed");
        
        for (String fileName : fileNames) {
            assertTrue(Files.exists(archiveDir.resolve(fileName)), 
                      "File should exist in archive: " + fileName);
        }
    }
}
