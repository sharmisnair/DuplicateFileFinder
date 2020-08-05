package com.finder;

import com.finder.files.exceptions.FilesProcessorException;
import java.io.IOException;

public class Main {

  private static final String DEFAULT_FILE_PATH = "src/main/resources/Code_Test.zip";

  public static void main(String[] args) {

    String compressedFilePath = args.length != 1 ? DEFAULT_FILE_PATH : args[0];
    printDuplicateFiles(compressedFilePath);

  }

  public static void printDuplicateFiles(String compressedFilePath) {
    DuplicateFinder duplicateFinder ;

    try {

      duplicateFinder = new DuplicateFinder(compressedFilePath);
      duplicateFinder.findAndDisplayDuplicates();

    } catch (IOException ioException) {
      System.err.println("ERROR in IO processing of source file: " + ioException.getMessage());

    } catch (FilesProcessorException fpException) {
      System.err
          .println("Error in finding duplicates in given source file: " + fpException.getMessage());
    }
  }
}
