package com.example.home_security_api.controller;

import com.example.home_security_api.config.AppConfig;
import com.example.home_security_api.model.FileData;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private AppConfig appConfig;
    private String directory; 
    private NumberFormat nf = new DecimalFormat("#.00");
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss a");

    public FileController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @GetMapping
    public ResponseEntity<List<FileData>> getFiles(@RequestParam(required = false) String selectedDate) {
        
        if (selectedDate == null) {
            String currenDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            setDirectory(currenDate);
        } else {
            setDirectory(selectedDate);
        }

        File folder = new File(directory);
        if (!folder.exists() || !folder.isDirectory()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<FileData> fileData = Arrays.stream(folder.listFiles())
            .filter(File::isFile)
            .map(file -> 
                    new FileData()
                        .setName(file.getName())
                        .setCameraNr(Integer.parseInt(file.getName().split("_")[1]))
                        .setType(file.getName().split("\\.")[1])
                        .setSize(Double.parseDouble(nf.format((double) file.length() / 1024 / 1024)))
                        .setPath(file.getPath())
                        .setCreated(df.format(new Date(file.lastModified())))
            )
            .collect(Collectors.toList());
            
            return new ResponseEntity<>(fileData, HttpStatus.OK);
        }
        
        @DeleteMapping()
        public void deleteFile(@RequestBody FileData file) {
            File fileDelete = new File(file.getPath());
            fileDelete.delete();
        }

    @PostMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestBody FileData file) throws IOException {

        Path filePath = Paths.get(file.getPath()).normalize();
        
        if (!filePath.startsWith(directory) || !Files.exists(filePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
            .body(resource);
    }

    @GetMapping("/video/{fileName}")
    public ResponseEntity<Resource> getVideo(@PathVariable String fileName) throws IOException {

        Path path = Paths.get(directory + appConfig.getEnvironment().getSeparator() + fileName);

        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(path.toUri());
        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(resource);
    }

    public void setDirectory(String date) {
        directory = appConfig.getEnvironment().getBasePath() + date;
    }
}