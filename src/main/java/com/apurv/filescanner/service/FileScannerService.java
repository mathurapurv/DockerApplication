package com.apurv.filescanner.service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.apurv.filescanner.config.FileScannerProperties;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileScannerService {
    private final FileScannerProperties properties;
    private final List<String> processedFiles = new ArrayList<>();
    private final List<WatchService> watchServices = new ArrayList<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private volatile boolean running = true;

    @Autowired
    public FileScannerService(FileScannerProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {

        if (!CollectionUtils.isEmpty(properties.getWatchDirectories())) {
            for (String dirPath : properties.getWatchDirectories()) {
                Path directoryPath = Paths.get(dirPath);
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) {
                    for (Path filePath : stream) {
                           if (Files.isRegularFile(filePath)) {
                                       processFile(filePath, directoryPath.resolve(properties.getArchiveSubfolder()));
                                    }
                    }
                } catch (IOException e) {
                    log.error("Error reading files in directory: " + directoryPath, e);
                }
            }
        }



        if (properties.getWatchDirectories() != null) {
            for (String dirPath : properties.getWatchDirectories()) {
                startWatching(Paths.get(dirPath));
            }
        }
    }

    private void startWatching(Path directory) {
        try {
            // Create archive directory if it doesn't exist
            Path archiveDir = directory.resolve(properties.getArchiveSubfolder());
            Files.createDirectories(archiveDir);

            WatchService watchService = FileSystems.getDefault().newWatchService();
            watchServices.add(watchService);

            directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            log.info("Watching directory for new files: {}", directory);

            executor.submit(() -> {
                try {
                    while (running) {
                        WatchKey key = watchService.poll(1, TimeUnit.SECONDS);
                        if (key != null) {
                            for (WatchEvent<?> event : key.pollEvents()) {
                                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                                    Path filePath = directory.resolve((Path) event.context());
                                    if (Files.isRegularFile(filePath)) {
                                        processFile(filePath, archiveDir);
                                    }
                                }
                            }
                            key.reset();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("File watcher thread interrupted", e);
                } catch (Exception e) {
                    log.error("Error in file watcher", e);
                }
            });
        } catch (IOException e) {
            log.error("Failed to initialize file watcher for directory: " + directory, e);
        }
    }

    private synchronized void processFile(Path filePath, Path archiveDir) {
        try {
            // Read file content

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            

            processedFiles.addAll(Files.lines(filePath)
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList()));   
                            
            log.info("Processed file: {}", filePath);

            // Move to archive
            Path targetPath = archiveDir.resolve(filePath.getFileName());
            Files.move(filePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Moved file to archive: {}", targetPath);
        } catch (IOException e) {
            log.error("Error processing file: " + filePath, e);
        }
    }

    public synchronized List<String> getProcessedFiles() {
        return new ArrayList<>(processedFiles);
    }

    @PreDestroy
    public void cleanup() {
        running = false;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        for (WatchService watchService : watchServices) {
            try {
                watchService.close();
            } catch (IOException e) {
                log.error("Error closing watch service", e);
            }
        }
    }
}
