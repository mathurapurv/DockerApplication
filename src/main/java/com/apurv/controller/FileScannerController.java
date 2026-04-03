package com.apurv.controller;

import com.apurv.filescanner.service.FileScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileScannerController {

    private final FileScannerService fileScannerService;

    @Autowired
    public FileScannerController(FileScannerService fileScannerService) {
        this.fileScannerService = fileScannerService;
    }

    @GetMapping("/processed")
    public List<String> getProcessedFiles() {
        return fileScannerService.getProcessedFiles();
    }
}
