package com.example.home_security_api.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
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
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.home_security_api.config.AppConfig;
import com.example.home_security_api.model.FileData;
import com.example.home_security_api.repository.FileDataRepository;

@Service
public class FileService {

    private String directory;
    private NumberFormat nf = new DecimalFormat("#.00");
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss a");
    
    private FileDataRepository fileDataRepository;
    private final AppConfig appConfig;

    public FileService(FileDataRepository fileDataRepository, AppConfig appConfig) {
        this.fileDataRepository = fileDataRepository;
        this.appConfig = appConfig;
    }

    public Optional<List<FileData>> getFiles(String selectedDate) {
        
        if (selectedDate == null) {
            String currenDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            setDirectory(currenDate);
        } else {
            setDirectory(selectedDate);
        }

        File folder = new File(directory);
        if (!folder.exists() || !folder.isDirectory()) {
            if (selectedDate == null) 
                folder.mkdir();
            else return Optional.empty();
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

        return Optional.of(fileData); 
    }

    @Scheduled(fixedRate = 600000)
    public void updateDatabase() throws IOException {

        List<FileData> files = getFiles(null).orElseThrow(() -> new IOException("No file directory found"));
        files = files.stream()
            .filter(file -> !fileDataRepository.existsByName(file.getName()))
            .collect(Collectors.toList());
        fileDataRepository.saveAll(files);

        System.out.println("Database updated with FileData = " + files);
    }

    public void setDirectory(String date) {
        directory = appConfig.getEnvironment().getBasePath() + date;
    }

    public Optional<URI> getFileUri(FileData file) {
        Path filePath = Paths.get(file.getPath()).normalize();

        if (!filePath.startsWith(directory) || !Files.exists(filePath)) {
            return Optional.empty();
        }

        return Optional.of(filePath.toUri());
    }

    public Path getFilePath(String fileName) {
        return Paths.get(directory).resolve(fileName).normalize();
    }

    public void deleteFile(FileData file) {
        Path filePath = getFilePath(file.getName());
        
        try {
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
