import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Day1 extends Day {

    void part1() throws IOException {
        final List<Integer> lines = Utils.readLinesAsInt("day1.txt");
        final int target = 2020;
        Collections.sort(lines);
        for (int i = 0; i < lines.size() - 1; i++) {
            for (int j = i + 1; j < lines.size(); j++) {
                int a = lines.get(i);
                int b = lines.get(j);
                if (a + b == target) {
                    System.out.println(a + "+" + b + "=" + target + ", " + a + "*" + b + "=" + (a * b));
                }
            }
        }
    }

    void part2() throws IOException {
        final List<Integer> lines = Utils.readLinesAsInt("day1.txt");
        final int target = 2020;
        Collections.sort(lines);
        for (int i = 0; i < lines.size() - 2; i++) {
            for (int j = i + 1; j < lines.size() - 1; j++) {
                for (int k = j + 1; k < lines.size(); k++) {
                    int a = lines.get(i);
                    int b = lines.get(j);
                    int c = lines.get(k);
                    if (a + b + c == target) {
                        System.out.println(a + "+" + b + "+" + c + "=" + target + ", " + a + "*" + b + "*" + c + "=" + (a * b * c));
                    }
                }
            }
        }
    }

}
