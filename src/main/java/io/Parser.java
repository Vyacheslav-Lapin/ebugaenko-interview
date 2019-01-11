package io;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

import static lombok.AccessLevel.PRIVATE;

/**
 * This class is thread safe.
 */
@Slf4j
@FieldDefaults(level = PRIVATE)
public class Parser {
  File file;
  
  public Parser(final File file) {
    this.file = file;
  }

  public File getFile() {
    return file;
  }

  public String getContent() throws IOException {
    String output;
    try (InputStream i = new FileInputStream(file)) {
      output = "";
      int data;
      while ((data = i.read()) > 0) {
        output += (char) data;
      }
    }
    return output;
  }

  public String getContentWithoutUnicode() throws IOException {
    try (InputStream i = new FileInputStream(file)) {
      String output = "";
      int data;
      while ((data = i.read()) > 0) {
        if (data < 0x80) {
          output += (char) data;
        }
      }
      return output;
    }
  }

  public void saveContent(String content) throws IOException {
    try (OutputStream o = new FileOutputStream(file)) {
      for (int i = 0; i < content.length(); i += 1) {
        o.write(content.charAt(i));
      }
    }
  }
}
