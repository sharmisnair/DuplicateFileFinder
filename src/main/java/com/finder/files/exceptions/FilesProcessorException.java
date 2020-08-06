package com.finder.files.exceptions;

public class FilesProcessorException extends Exception {

  private FilesProcessorErrCode errorCode;

  public FilesProcessorException(FilesProcessorErrCode errorCode, String extraMsg) {
    super(errorCode.toString() + ": " + extraMsg);
    this.errorCode = errorCode;
  }

  public FilesProcessorException(FilesProcessorErrCode errorCode) {
    super(errorCode.toString());
    this.errorCode = errorCode;
  }
}
