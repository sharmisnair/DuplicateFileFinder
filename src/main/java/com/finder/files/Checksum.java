package com.finder.files;

import com.finder.files.exceptions.FilesProcessorErrCode;
import com.finder.files.exceptions.FilesProcessorException;
import com.google.common.io.BaseEncoding;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Checksum {

  private MessageDigest digest;

  public Checksum(String checksumType) throws FilesProcessorException {
    try {
      digest = MessageDigest.getInstance(checksumType);
    } catch (NoSuchAlgorithmException e) {
      throw new FilesProcessorException(FilesProcessorErrCode.NO_CHECKSUM_TYPE, checksumType);
    }
  }

  public String getChecksum() {
    String checksum = BaseEncoding.base16().encode(digest.digest());
    digest.reset();
    return checksum;
  }

  public void update(byte[] block, int length) {
    digest.update(block, 0, length);
  }
}
