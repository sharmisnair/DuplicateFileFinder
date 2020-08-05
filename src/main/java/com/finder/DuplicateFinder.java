package com.finder;

import com.finder.files.Checksum;
import com.finder.files.FilesProcessorFactory;
import com.finder.files.exceptions.FilesProcessorException;
import com.finder.files.FilesProcessor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class DuplicateFinder {

  FilesProcessor fileProcessor;
  Checksum checksum;
  String originalFilePath;
  private static final String CHECKSUM_TYPE = "SHA-256";

  public DuplicateFinder(String originalFilePath) throws FilesProcessorException {
    this.originalFilePath = originalFilePath;
    checksum = new Checksum(CHECKSUM_TYPE);

    FilesProcessorFactory filesProcessorFactory = new FilesProcessorFactory();
    String fileFormat = filesProcessorFactory.getFileType(originalFilePath);
    fileProcessor = filesProcessorFactory.getFileProcessorByType(fileFormat, checksum);
  }

  public void findAndDisplayDuplicates() throws IOException, FilesProcessorException {

    Map<String, ArrayList<String>> filesMap = getDuplicateFilesMap();

    displayDuplicates(filesMap);
  }

  public Map<String, ArrayList<String>> getDuplicateFilesMap() throws IOException, FilesProcessorException {

    fileProcessor.processFiles(originalFilePath);

    Map<String, ArrayList<String>> filesMap = fileProcessor.getChecksumAndFilesMap();

    return filterDuplicatesInMap(filesMap);
  }

  public Map<String, ArrayList<String>> filterDuplicatesInMap(Map<String, ArrayList<String>> filesMap) {

    return filesMap.entrySet().stream()
        .filter(fileEntry -> fileEntry.getValue().size() > 1)
        .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
  }

  private void displayDuplicates(Map<String, ArrayList<String>> filesMap) {

    filesMap.forEach((fileHash, duplicateFiles) -> {
      System.out.println("\nFound a duplicate:");
      duplicateFiles.forEach(System.out::println);
    });
  }

}
