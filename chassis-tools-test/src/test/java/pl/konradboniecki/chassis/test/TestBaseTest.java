package pl.konradboniecki.chassis.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class TestBaseTest extends TestBase {

    private String stringThatShouldBeVisibleInOneTestOnly = "this string should not be visible";

    @Test
    @DisplayName("File content is being returned")
    public void givenFileWithContent_whenGetContentAsString_thenContentReturned() throws FileNotFoundException {
        String fileContent = getFileContentAsString("FileToCheckContent", "txt");
        assertThat(fileContent).isEqualTo("test content {}\ntest content 2");
    }

    @Test
    @DisplayName("System.out is replaced")
    public void givenReplacedStreams_whenSystemOut_thenItCanBeRetrieved(){
        String testString = "this is test string";
        System.out.println(testString);
        assertThat(getLog()).isEqualTo(testString);

        System.out.println(stringThatShouldBeVisibleInOneTestOnly);
        assertThat(getLog()).isEqualTo(testString + stringThatShouldBeVisibleInOneTestOnly);
    }

    @Test
    @DisplayName("Streams are reverted")
    public void givenRevertedStreams_whenGetLog_thenPreviousDataIsRemoved() throws IOException {
        revertStreamsForLogs();
        assertThat(getLog()).doesNotContain(stringThatShouldBeVisibleInOneTestOnly);
    }
}
