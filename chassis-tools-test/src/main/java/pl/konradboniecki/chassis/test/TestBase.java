package pl.konradboniecki.chassis.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class TestBase {
    protected static PrintStream sysOut;
    protected static ByteArrayOutputStream outContent;

    @BeforeEach
    protected void prepareStreamsForLogs(){
        sysOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    protected void revertStreamsForLogs() throws IOException {
        System.setOut(sysOut);
        outContent.close();
        outContent.reset();
    }

    protected static String getLog(){
        return outContent.toString()
                .replaceAll("\n","")
                .replaceAll("\r","");
    }

    protected String getFileContentAsString(String filename, String extension) throws FileNotFoundException {
        File file = new File(getClass().getClassLoader().getResource(filename + "." + extension).getFile());
        assertTrue(file.exists());

        StringBuilder result = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line);
                if (scanner.hasNextLine()) {
                    result.append("\n");
                }
            }
        }
        return result.toString();
    }
}
