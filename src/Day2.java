import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day2 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day2.txt");
        int valid = 0;
        for (String line : lines) {
            if (validate1(extractDetails(line))) {
                valid++;
            }
        }
        System.out.println(valid + " valid passwords");
    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day2.txt");
        int valid = 0;
        for (String line : lines) {
            if (validate2(extractDetails(line))) {
                valid++;
            }
        }
        System.out.println(valid + " valid passwords");
    }

    private static Details extractDetails (final String line) {
        String[] parts = line.split(":\\s+");
        final String password = parts[1];
        parts = parts[0].split("\\s+");
        final char letter = parts[1].charAt(0);
        parts = parts[0].split("-");
        final int lower = Integer.parseInt(parts[0]);
        final int upper = Integer.parseInt(parts[1]);
        return new Details(password, letter, lower, upper);
    }

    private static boolean validate1 (final Details details) {
        final Map<Character, Long> charMap = details.password
            .codePoints()
            .mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        final Long letterOccurrence = charMap.get(details.letter);
        return letterOccurrence != null && letterOccurrence >= details.lower && letterOccurrence <= details.upper;
    }

    private static boolean validate2 (final Details details) {
        return details.password.charAt(details.lower - 1) == details.letter ^ details.password.charAt(details.upper - 1) == details.letter;
    }

    private static final class Details {
        String password;
        char letter;
        int lower;
        int upper;

        Details (final String password, final char letter, final int lower, final int upper) {
            this.password = password;
            this.letter = letter;
            this.lower = lower;
            this.upper = upper;
        }
    }

}
