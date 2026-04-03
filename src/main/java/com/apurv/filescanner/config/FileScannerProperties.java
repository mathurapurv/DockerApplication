package com.apurv.filescanner.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "file.scanner")
public class FileScannerProperties {
    private List<String> watchDirectories;
    private String archiveSubfolder = "Archive"; // default value set in the property
}
