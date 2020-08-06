package com.finder.files;

import com.finder.files.exceptions.FilesProcessorException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


public class ChecksumTest {

  @Test
  public void Checksum_invalidChecksum() {
    assertThatExceptionOfType(FilesProcessorException.class)
        .isThrownBy(() -> new Checksum("random"))
        .withMessageStartingWith("Invalid Checksum algorithm used");
  }

  @Test
  public void Checksum_SHA256() throws FilesProcessorException {
    Checksum checksum = new Checksum("SHA-256");
    String str = "example";
    byte[] bytes = str.getBytes();
    checksum.update(bytes, bytes.length);
    assertThat(checksum.getChecksum()).isEqualTo("50D858E0985ECC7F60418AAF0CC5AB587F42C2570A884095A9E8CCACD0F6545C");
  }
}
