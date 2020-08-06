package com.finder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.finder.files.exceptions.FilesProcessorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.junit.Test;

public class DuplicateFinderTest {

  @Test
  public void getDuplicateFilesMapTest_validZipTriplicates() throws IOException, FilesProcessorException {
    //Testing a file with triplicates and another unrelated file
    String compressedFilePath = "src/test/resources/3DupsAnd1File.zip";
    DuplicateFinder duplicateFinder = new DuplicateFinder(compressedFilePath);
    Map<String, ArrayList<String>> filesMap = duplicateFinder.getDuplicateFilesMap();

    assertThat(filesMap.entrySet().size()).isEqualTo(1);
    Map.Entry<String, ArrayList<String>> triplicates = filesMap.entrySet().iterator().next();
    assertThat(triplicates.getValue().size()).isEqualTo(3);
    assertThat(triplicates.getValue().contains("3DupsAnd1File/mew1DiffDirectory.jpg")).isEqualTo(true);
    assertThat(triplicates.getValue().contains("3DupsAnd1File/SampleTest/mew1.jpg")).isEqualTo(true);
    assertThat(triplicates.getValue().contains("3DupsAnd1File/SampleTest/mew2.jpg")).isEqualTo(true);
    assertThat(triplicates.getValue().contains("3DupsAnd1File/SampleTest/new.jpg")).isEqualTo(false);
  }

  @Test
  public void getDuplicateFilesMapTest_emptyZip() throws IOException, FilesProcessorException {
    //Testing an empty zip file
    String compressedFilePath = "src/test/resources/emptyzip.zip";
    DuplicateFinder duplicateFinder = new DuplicateFinder(compressedFilePath);
    Map<String, ArrayList<String>> filesMap = duplicateFinder.getDuplicateFilesMap();

    assertThat(filesMap.entrySet().size()).isEqualTo(0);
  }

  @Test
  public void getDuplicateFilesMapTest_invalidFormatInput() {
    String compressedFilePath = "src/test/resources/3DupsAnd1File.tar";
    assertThatExceptionOfType(FilesProcessorException.class)
        .isThrownBy(() -> new DuplicateFinder(compressedFilePath))
        .withMessageStartingWith("Given file in unsupported format");
  }
}
