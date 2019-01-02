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

  public synchronized File getFile() {
    return file;
  }

  public synchronized void setFile(File f) {
    file = f;
  }

  public String getContent() throws IOException {
    InputStream i = new FileInputStream(file);
    String output = "";
    int data;
    while ((data = i.read()) > 0) {
      output += (char) data;
    }
    return output;
  }

  public String getContentWithoutUnicode() throws IOException {
    InputStream i = new FileInputStream(file);
    String output = "";
    int data;
    while ((data = i.read()) > 0) {
      if (data < 0x80) {
        output += (char) data;
      }
    }
    return output;
  }

  public void saveContent(String content) throws IOException {
    OutputStream o = new FileOutputStream(file);
    for (int i = 0; i < content.length(); i += 1) {
      o.write(content.charAt(i));
    }
  }
}
