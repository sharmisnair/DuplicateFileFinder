package com.finder.files.exceptions;

public enum FilesProcessorErrCode {
  NO_CHECKSUM_TYPE("Invalid Checksum algorithm used"),
  UNSUPPORTED_FILE_FORMAT("Given file in unsupported format"),
  FILE_NOT_IN_TARGET_DIR("File entry is outside of the target dir causing potential zipslip vulnerability"),
  ;

  String errorMessage;

  FilesProcessorErrCode(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String toString() {
    return this.errorMessage;
  }
}
