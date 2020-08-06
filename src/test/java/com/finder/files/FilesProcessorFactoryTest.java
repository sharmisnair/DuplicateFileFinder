package com.finder.files;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.finder.files.exceptions.FilesProcessorException;
import org.junit.BeforeClass;
import org.junit.Test;

public class FilesProcessorFactoryTest {

  private static Checksum checksum;

  @BeforeClass
  public static void setUp() throws FilesProcessorException {
    checksum = new Checksum("SHA-256");
  }

  @Test
  public void getFileTypeTest() {
    FilesProcessorFactory factory = new FilesProcessorFactory();
    String filePath;

    filePath = "/def/ghi/abc.zip";
    assertThat(factory.getFileType(filePath)).isEqualTo("ZIP");

    filePath = "/def/ghi/abc.ZIP";
    assertThat(factory.getFileType(filePath)).isEqualTo("ZIP");

    filePath = "/def/ghi/abc.tar";
    assertThat(factory.getFileType(filePath)).isEqualTo("TAR");

    filePath = "/def/ghi/abc";
    assertThat(factory.getFileType(filePath)).isEqualTo(null);

    // This method doesn't handle complex file extensions - to be extended on a need-to basis.
    filePath = "/def/ghi/abc.tar.gz";
    assertThat(factory.getFileType(filePath)).isEqualTo("GZ");
  }

  @Test
  public void getFileProcessorByTypeTest_validZip() throws FilesProcessorException {
    FilesProcessorFactory factory = new FilesProcessorFactory();
    FilesProcessor filesProcessor = factory.getFileProcessorByType("ZIP", checksum);
    assertThat(filesProcessor.getClass()).isEqualTo(ZipFilesProcessor.class);
  }

  @Test
  public void getFileProcessorByTypeTest_invalidFormat() throws FilesProcessorException {
    FilesProcessorFactory factory = new FilesProcessorFactory();
    assertThatExceptionOfType(FilesProcessorException.class)
        .isThrownBy(() -> factory.getFileProcessorByType("", checksum))
        .withMessageStartingWith("Given file in unsupported format");

    assertThatExceptionOfType(FilesProcessorException.class)
        .isThrownBy(() -> factory.getFileProcessorByType("tar", checksum))
        .withMessageStartingWith("Given file in unsupported format");
  }

}
