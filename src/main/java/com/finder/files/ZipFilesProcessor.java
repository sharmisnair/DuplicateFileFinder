package com.finder.files;

import com.finder.files.exceptions.FilesProcessorErrCode;
import com.finder.files.exceptions.FilesProcessorException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFilesProcessor extends FilesProcessor {

  private static final String ZIP_FILE_NAME_DELIM = "/";
  private static final String FILE_EXT = "ZIP";

  public ZipFilesProcessor(Checksum checksum) {
    super(checksum);
  }

  @Override
  public String getFileExt() {
    return FILE_EXT;
  }

  /**
   * Process all files within given source Zip And calls processInputStreamForFile on each file
   * entry which calculates checksum for file and adds to hashmap
   *
   * @param sourcePath
   * @throws IOException
   * @throws FilesProcessorException
   */
  @Override
  public void processFiles(String sourcePath)
      throws IOException, FilesProcessorException {
    FileInputStream fis = null;
    ZipInputStream zis = null;
    ZipEntry zipEntry = null;

    checkAndSetFilePaths(sourcePath);

    try {
      fis = new FileInputStream(sourcePath);
      zis = new ZipInputStream(fis);
      zipEntry = zis.getNextEntry();

      while (zipEntry != null) {

        if (!zipEntry.isDirectory()) {
          processZipInputStreamForFile(zipEntry.getName(), zis);
        }
        zis.closeEntry();
        zipEntry = zis.getNextEntry();
      }

    } finally {
      closeZipFiles(zis);
      super.closeInputStream(fis);
    }

  }

  /**
   * Validate and Process given zip file entry Calls processInputStreamForFile which calculates
   * checksum and adds to hashmap
   *
   * @param fileName String
   * @param is       ZipInputStream
   * @throws IOException
   * @throws FilesProcessorException
   */
  public void processZipInputStreamForFile(String fileName, ZipInputStream is)
      throws IOException, FilesProcessorException {
    checkZipFileEntry(fileName);
    super.processInputStreamForFile(fileName, is);
  }

  /**
   * Quietly close ZipInputStream
   *
   * @param zis ZipInputStream object
   */
  public void closeZipFiles(ZipInputStream zis) {
    if (zis != null) {
      try {
        zis.closeEntry();
      } catch (IOException ignored) {
      }
    }
    super.closeInputStream(zis);
  }

  /**
   * Validations on given file entry in Zipped folder
   *
   * @param fileName
   * @throws FilesProcessorException
   * @throws IOException
   */
  public void checkZipFileEntry(String fileName)
      throws FilesProcessorException, IOException {

    // Check for zip slip vulnerability
    if (!isFileInOriginalDirPath(fileName)) {
      throw new FilesProcessorException(FilesProcessorErrCode.FILE_NOT_IN_TARGET_DIR);
    }
  }

  /**
   * Construct valid file name stripping special characters so that File.class doesn't crash
   */
  private String constructSupportedFileName(String origFileName) {
    return Arrays
        .stream(origFileName.split(ZIP_FILE_NAME_DELIM))
        .map(s -> s.replaceAll("[\\\\/:*?\"<>!|]", "_"))
        .collect(Collectors
            .joining(ZIP_FILE_NAME_DELIM));
  }

  /**
   * Check for ZIP SLIP vulnerability Whether given zip file entry is not inside the source
   * directory
   */
  private boolean isFileInOriginalDirPath(String fileName) throws IOException {

    String parentDirPath = getParentDirPath();
    File OriginalDir = new File(getParentDirPath());

    File currentFile = new File(OriginalDir, constructSupportedFileName(fileName));
    String canonicalCurrentFile = currentFile.getCanonicalPath();

    return canonicalCurrentFile.startsWith(parentDirPath);
  }

}
