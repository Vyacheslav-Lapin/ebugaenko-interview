package io;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class ParserTest {

  static final String CONTENT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
    "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in " +
    "reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa " +
    "qui officia deserunt mollit anim id est laborum.";

  Parser parser = new Parser();
  Path path;
  String fileContent;

  @BeforeEach
  @SneakyThrows
  void setUp() {
    String pathname = ParserTest.class.getResource("/1.txt").getFile();
    parser.setFile(new File(pathname));
    path = Paths.get(pathname);
    fileContent = Files.readString(path);
  }

  @Test
  @SneakyThrows
  @DisplayName("\"getContent\" method works correctly")
  void testGetContent() {
    assertThat(parser.getContent())
      .isEqualTo(fileContent);
  }

  @Test
  @SneakyThrows
  @DisplayName("\"getContentWithoutUnicode\" method works correctly")
  void testGetContentWithoutUnicode() {
    assertThat(parser.getContentWithoutUnicode())
      .isEqualTo(fileContent);
  }

  @Test
  @SneakyThrows
  @DisplayName("\"saveContent\" method works correctly")
  void testSaveContent() {
    parser.saveContent(CONTENT);
    assertThat(Files.readString(path)).isEqualTo(CONTENT);
    Files.writeString(path, fileContent);
  }

//  @Test
//  @DisplayName("\"getContent\" method is not closing resource")
//  void testGetContentIsNotClosingResource() {
//    var fileNotFoundException = assertThrows(FileNotFoundException.class, () -> {
//        for (int i = 0; i < 20_000; i++)
//          testGetContent();
//      }
//    );
//    assertThat(fileNotFoundException.getMessage())
//      .endsWith("(Too many open files)");
//  }

  @Test
  @DisplayName("\"getContent\" method is closing resource")
  void testGetContentIsClosingResource() {
    for (int i = 0; i < 20_000; i++)
      testGetContent();
  }
}
