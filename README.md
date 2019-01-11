# Разбор кода из интервью Е.Бугаенко
Приветствую! В [классе, представленном Егором Бугаенко](https://gist.github.com/yegor256/6335539) для обсуждения на [публичном интервью на нашем канале](https://youtu.be/UUhB4rVlIoU) было интересно покопаться. Как и сказал Егор, там содержится множество ошибок, влекущих возможные проблемы. Мне удалось найти некоторое кол-во из них, о чём в дальнейшем и пойдёт речь, но для начала выложу этот код на GitHub и покажу в виде тестов элементарные сценарии, как с ним можно было бы работать.

Затем я пойду по обнаруженным проблемам, буду выявлять их по TDD - т.е. при помощи написания новых тестов, после чего буду измененять код так, что бы проблема ушла.

## №1 Не закрытые потоки ввода-вывода  
Итак, первая проблема - потоки (`InputStream`, `OutputStream`) в методах `getContent`, `getContentWithoutUnicode` и `saveContent` не закрыты:
```java
public String getContent() throws IOException {
  InputStream i = new FileInputStream(file);
  //...
}
```
- К чему приведёт?
   - Дескриптор файла в ОС не будет освобождён и никакой другой код не сможет открыть этот же файл повторно
      + ОС в лице файловой системы может самортизировать этот процесс, но ограничение на кол-во открытий всё равно будет
   - Тест, который это выявляет:
     ```java
     @Test
     @DisplayName("\"getContent\" method is not closing resource")
     void testGetContentIsNotClosingResource() {
       var fileNotFoundException = assertThrows(FileNotFoundException.class, () -> {
           for (int i = 0; i < 20_000; i++)
             assertThat(parser.getContent())
                 .isEqualTo(fileContent);
         }
       );
       assertThat(fileNotFoundException.getMessage())
         .endsWith("(Too many open files)");
     }
     ```
     _Число 20_000 получено эмпирически для моего ПК и ОС (Mac Mojave i7 2.9GHz 4-х ядерный), у Вас может быть необходимо поставить больше, либо наоборот - хватит меньшего числа_  
     Перепишем тест, что бы он отслеживал правильное выполнение и падал, когда оно неправильное:
     ```java
     @Test
     @DisplayName("\"getContent\" method is closing resource")
     void testGetContentIsClosingResource() {
       for (int i = 0; i < 20_000; i++)
         testGetContent();
     }
     ```
- Что делать?
   - В Java до 6-ки включительно - закрыть в блоке finally, перед этим проверив на `null`
     ```java
     public String getContentWithoutUnicode() throws IOException {
       InputStream i = null;
       try {
         i = new FileInputStream(file);
         //...
       } finally {
         if (i != null)
           i.close();
       }
     }
     ```
  - В Java 7+ - `try-with-resources`:
    ```java
    public String getContent() throws IOException {
      try (InputStream i = new FileInputStream(file)) {
        //...
      }
    }
    ```
  - Если Java 6+ и к проекту подключён Lombok - поставить перед объявлением аннотацию `@Cleanup`:
    ```java
    public void saveContent(String content) throws IOException {
      @Cleanup OutputStream o = new FileOutputStream(file);
      //...
    }
    ```
Любой из этих 3-х способов решает проблему. Я выбрал наиболее традиционный - вставил блок `try-with-resources`. 
