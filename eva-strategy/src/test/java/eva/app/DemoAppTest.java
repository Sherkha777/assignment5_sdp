package eva.app;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoAppTest {

    private String run(String... lines) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BufferedReader in = new BufferedReader(new StringReader(String.join("\n", lines) + "\n"));
        new DemoApp(in, new PrintStream(bytes)).run();
        return bytes.toString();
    }

    @Test
    void syncStrategiesCanBeComparedSelectedAndSwitched() throws IOException {
        String output = run(
                "8", "Shinji", "14",
                "1", "Shinji", "30", "2",
                "2", "Shinji", "30", "2", "23:00",
                "2", "Rei", "30", "15", "12:00",
                "3", "2", "Shinji", "30", "2",
                "0");
        assertTrue(output.contains("stress-only"));
        assertTrue(output.contains("selector chose fatigue-adjusted"));
        assertTrue(output.contains("selector chose veteran-bonus"));
        assertTrue(output.contains("current strategy: veteran-bonus"));
        assertTrue(output.contains("fatigue-adjusted -> 55.0"));
        assertTrue(output.contains("console closed"));
    }

    @Test
    void powerStrategiesCanBeComparedSelectedAndSwitched() throws IOException {
        String output = run(
                "4", "10", "8", "80",
                "5", "10", "8", "80",
                "7", "4",
                "5", "10", "8", "80",
                "6", "1", "10", "8", "80",
                "0");
        assertTrue(output.contains("grid-first"));
        assertTrue(output.contains("selector chose balanced"));
        assertTrue(output.contains("grid now offers 4.0 MW"));
        assertTrue(output.contains("selector chose battery-first"));
        assertTrue(output.contains("current strategy: battery-first"));
    }

    @Test
    void badInputIsReportedWithoutStoppingTheConsole() throws IOException {
        String output = run("1", "Shinji", "lots", "1", "Shinji", "150", "2", "2", "Rei", "10", "1",
                "25:99", "42", "0");
        assertTrue(output.contains("that was not a number"));
        assertTrue(output.contains("refused: stress must be between 0 and 100"));
        assertTrue(output.contains("unknown option"));
    }

    @Test
    void endOfInputClosesTheConsole() throws IOException {
        assertTrue(run().contains("console closed"));
    }
}
