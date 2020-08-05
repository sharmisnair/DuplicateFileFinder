package com.finder.files;

import com.finder.files.exceptions.FilesProcessorErrCode;
import com.finder.files.exceptions.FilesProcessorException;

public class FilesProcessorFactory {

  public FilesProcessor getFileProcessorByType(String format, Checksum checksum)
      throws FilesProcessorException {
    if (format != null && format.equals("ZIP")) {
      return new ZipFilesProcessor(checksum);
    }
    throw new FilesProcessorException(FilesProcessorErrCode.UNSUPPORTED_FILE_FORMAT);
  }

  public String getFileType(String sourcePath) {
    int extIndex = sourcePath.lastIndexOf('.');
    // If directory, extIndex is -1 so return null
    // Otherwise return the extension only eg., ZIP
    return extIndex == -1 ? null : sourcePath.substring(extIndex + 1).toUpperCase();
  }

}
