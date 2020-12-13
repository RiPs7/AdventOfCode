import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day5 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day5.txt");
        int highestId = 0;
        for (final String line : lines) {
            int id = decodeSeat(line);
            if (id > highestId) {
                highestId = id;
            }
        }
        System.out.println("Highest seat ID is: " + highestId);
    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day5.txt");
        final List<Integer> seatIds = lines.stream().map(Day5::decodeSeat).sorted(Integer::compareTo).collect(Collectors.toList());
        for (int i = 0; i < seatIds.size() - 1; i++) {
            if (seatIds.get(i + 1) - seatIds.get(i) > 1) {
                System.out.println("My seat ID is: " + (seatIds.get(i) + 1));
                break;
            }
        }
    }

    private static int decodeSeat(final String code) {
        final String firstPart = code.substring(0, 7);
        final String lastPart = code.substring(7);
        final String firstPartDecodedBase2 = firstPart.replace("F", "0").replace("B", "1");
        final String lastPartDecodedBase2 = lastPart.replace("L", "0").replace("R", "1");
        final int firstPartDecoded = Integer.parseInt(firstPartDecodedBase2, 2);
        final int lastPartDecoded = Integer.parseInt(lastPartDecodedBase2, 2);
        return firstPartDecoded * 8 + lastPartDecoded;
    }
}
