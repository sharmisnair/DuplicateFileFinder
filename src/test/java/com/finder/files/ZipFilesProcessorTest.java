package com.finder.files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.finder.files.exceptions.FilesProcessorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.BeforeClass;
import org.junit.Test;

public class ZipFilesProcessorTest {

  private static Checksum checksum;

  @BeforeClass
  public static void setUp() throws FilesProcessorException {
    checksum = new Checksum("SHA-256");
  }

  @Test
  public void processFilesTest_validZipValidData() throws IOException, FilesProcessorException {
    FilesProcessor filesProcessor = new ZipFilesProcessor(checksum);
    String parentDir = "src/test/resources";
    String sourcePath = parentDir + "/3DupsAnd1File.zip";
    filesProcessor.processFiles(sourcePath);
    HashMap<String, ArrayList<String>> filesMap = filesProcessor.getChecksumAndFilesMap();
    // Verify that there are 2 unique entries (the triplicates forming 1 entry)
    assertThat(filesMap.entrySet().size()).isEqualTo(2);

    // Verify the path which forms the canonical path to source file and parent dir has expected name
    // full path will be system dependent to unix or windows so we will verify just the file name
    assertThat(filesProcessor.getSourcePath().endsWith("3DupsAnd1File.zip")).isEqualTo(true);
    assertThat(filesProcessor.getParentDirPath().endsWith("resources")).isEqualTo(true);
  }

  @Test
  public void processFilesTest_invalidInput() {
    FilesProcessor filesProcessor = new ZipFilesProcessor(checksum);

    String sourcePath1 = "src/test/resources/3DupsAnd1File";
    assertThatExceptionOfType(FilesProcessorException.class)
        .isThrownBy(() -> filesProcessor.processFiles(sourcePath1))
        .withMessage("Given file in unsupported format");

    String sourcePath2 = "src/test/resources/3DupsAnd1File.tar";
    assertThatExceptionOfType(FilesProcessorException.class)
        .isThrownBy(() -> filesProcessor.processFiles(sourcePath2))
        .withMessage("Given file in unsupported format");

  }

  @Test
  public void processFilesTest_nonPhotoFilesInZip() throws IOException, FilesProcessorException {
    FilesProcessor filesProcessor = new ZipFilesProcessor(checksum);

    // Has 1 photo, 2 duplicate zipped files of different names and same photo contents , 1 empty zip
    // expecting to find 3 checksum entries identifying the 2 duplicates as common checksum
    String sourcePath3 = "src/test/resources/zipOfDuplicateZips.zip";

    filesProcessor.processFiles(sourcePath3);
    HashMap<String, ArrayList<String>> filesMap = filesProcessor.getChecksumAndFilesMap();
    assertThat(filesMap.entrySet().size()).isEqualTo(3);
  }

  @Test
  public void checkZipFileEntryTest_valid() throws IOException, FilesProcessorException {
    ZipFilesProcessor filesProcessor = new ZipFilesProcessor(checksum);
    filesProcessor.checkAndSetFilePaths("src/test/resources/3DupsAnd1File.zip");
    String fileEntryName = "3DupsAnd1File/SampleTest/mew1.jpg";
    // fileEntryName inside given zip target directory - no exceptions thrown.
    filesProcessor.checkZipFileEntry(fileEntryName);
  }

  @Test
  public void checkZipFileEntryTest_zipSlipVulnerability() throws IOException, FilesProcessorException {
    ZipFilesProcessor filesProcessor = new ZipFilesProcessor(checksum);
    filesProcessor.checkAndSetFilePaths("src/test/resources/3DupsAnd1File.zip");

    String fileEntryName1 = "../../../../../../3DupsAnd1File/SampleTest/mew1.jpg";
    // fileEntryName navigating outside given zip target directory
    assertThatExceptionOfType(FilesProcessorException.class)
        .isThrownBy(() -> filesProcessor.checkZipFileEntry(fileEntryName1))
        .withMessage("File entry is outside of the target dir causing potential zipslip vulnerability");

    String fileEntryName2 = "3DupsAnd1File/../../../../../evil.sh";
    // fileEntryName navigating outside given zip target directory
    assertThatExceptionOfType(FilesProcessorException.class)
        .isThrownBy(() -> filesProcessor.checkZipFileEntry(fileEntryName2))
        .withMessage("File entry is outside of the target dir causing potential zipslip vulnerability");
  }
}
