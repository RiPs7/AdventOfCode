import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public final class Utils {

    public static List<String> readLines (final String fileName) throws IOException {
        try (final InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName)) {
            if (is == null) throw new IOException();
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.toList());
            }
        }
    }

    public static List<Integer> readLinesAsInt (final String finalName) throws IOException {
        return readLines(finalName).stream().map(Integer::parseInt).collect(Collectors.toList());
    }
}
