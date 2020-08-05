package com.finder.files;

import com.finder.files.exceptions.FilesProcessorException;
import java.io.IOException;
import java.io.InputStream;

public interface FileProcessing {

  void processInputStreamForFile(String fileName, InputStream inputStream) throws IOException;
  void processFiles(String sourceArchivePath) throws IOException, FilesProcessorException;
  void closeInputStream(InputStream is);

}
