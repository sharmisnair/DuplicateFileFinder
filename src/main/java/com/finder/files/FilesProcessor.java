package com.finder.files;

import com.finder.files.exceptions.FilesProcessorErrCode;
import com.finder.files.exceptions.FilesProcessorException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class FilesProcessor implements FileProcessing {

  Checksum checksum;
  String parentDirPath;
  String sourcePath;
  HashMap<String, ArrayList<String>> hashedFileMap;
  public abstract String getFileExt();

  public FilesProcessor(Checksum checksum) {
    reset();
    this.checksum = checksum;
  }

  public void reset() {
    this.hashedFileMap = new HashMap<>();
    this.parentDirPath = null;
    this.sourcePath = null;
    this.checksum = null;
  }

  public HashMap<String, ArrayList<String>> getChecksumAndFilesMap() {
    return hashedFileMap;
  }
  private void setSourcePath(String sourcePath) { this.sourcePath = sourcePath; }
  private void setParentDirPath(String parentDirPath) { this.parentDirPath = parentDirPath; }
  public String getSourcePath() { return this.sourcePath; }
  public String getParentDirPath() { return this.parentDirPath; }

  public void checkAndSetFilePaths(String sourcePath) throws IOException, FilesProcessorException {
    String canonicalSourcePath = getCanonicalFilePath(sourcePath);
    if (!isValidFileFormat(canonicalSourcePath)) {
      throw new FilesProcessorException(FilesProcessorErrCode.UNSUPPORTED_FILE_FORMAT);
    }
    setSourcePath(getCanonicalFilePath(canonicalSourcePath));
    setParentDirPath(getCanonicalParentPath(canonicalSourcePath));
  }

  /**
   * Process each file entry by loading each block of the file to calculate unique checksum for the file.
   * Then also add checksum and filename into hashmap. Duplicate file contents to have same checksum.
   * @param fileName the name of the file
   * @param is InputStream object
   * @throws IOException
   */
  @Override
  public void processInputStreamForFile(String fileName, InputStream is)
      throws IOException {
    int FILE_BLOCK_SIZE = 4096;
    byte[] block = new byte[FILE_BLOCK_SIZE];
    int length = 0;
    while ((length = is.read(block)) > 0) {
      checksum.update(block, length);
    }
    addProcessedFile(fileName);
  }

  /**
   * Quietly close open InputStream
   * @param is InputStream
   */
  @Override
  public void closeInputStream(InputStream is) {
    if (is != null) {
      try {
        is.close();
      } catch (IOException ignored) {
      }
    }
  }

  private void addProcessedFile(String fileName) {
    String fileHash = checksum.getChecksum();
    addFileToHashMap(fileHash, fileName);
  }

  private void addFileToHashMap(String fileHash, String fileName) {
    ArrayList<String> fileList;
    if (hashedFileMap.containsKey(fileHash)) {
      fileList = hashedFileMap.get(fileHash);
    } else {
      fileList = new ArrayList<>();
    }
    fileList.add(fileName);
    hashedFileMap.put(fileHash, fileList);
  }

  private String getCanonicalFilePath(String filePath) throws IOException {
    File file = new File(Paths.get(filePath).toString());
    return file.getCanonicalPath();
  }

  private String getCanonicalParentPath(String filePath) throws IOException {
    File file = new File(Paths.get(filePath).getParent().toString());
    return file.getCanonicalPath();
  }

  private boolean isValidFileFormat(String sourcePath) {
    FilesProcessorFactory factory = new FilesProcessorFactory();
    String fileType = factory.getFileType(sourcePath);
    return fileType != null && fileType.equals(getFileExt());
  }

}
