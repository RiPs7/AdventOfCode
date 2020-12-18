import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day10 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day10.txt");
        // Get the lines as a sorted number list
        final List<Integer> numbers = lines.stream().map(Integer::parseInt).sorted().collect(Collectors.toList());
        // Add the charging outlet rate
        numbers.add(0, 0);
        // Add the built-in joltage adapter
        numbers.add(numbers.get(numbers.size() - 1) + 3);
        int count1JoltDiffs = 0;
        int count3JoltDiffs = 0;
        for (int i = 0; i < numbers.size() - 1; i++) {
            final int diff = numbers.get(i + 1) - numbers.get(i);
            if (diff == 1) {
                count1JoltDiffs++;
            } else if (diff == 3) {
                count3JoltDiffs++;
            }
        }
        System.out.println(count1JoltDiffs + " 1-Jolt differences AND " + count3JoltDiffs + " 3-Jolt differences" + " --> " + (count1JoltDiffs * count3JoltDiffs));
    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day10.txt");
        // Get the lines as a sorted number list
        final List<Integer> numbers = lines.stream().map(Integer::parseInt).sorted().collect(Collectors.toList());
        // Add the charging outlet rate
        numbers.add(0, 0);
        // Add the built-in joltage adapter
        numbers.add(numbers.get(numbers.size() - 1) + 3);
        System.out.println("There are " + getArrangements(new HashMap<>(), numbers) + " distinct ways to arrange the adapters");
    }

    // A dynamic, memoization-based, recursive approach, to generate all the arrangements
    static long getArrangements(final HashMap<String, Long> cache, final List<Integer> currentArrangement) {
        final String key = currentArrangement.stream().map(Object::toString).collect(Collectors.joining(","));
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        final long result = IntStream.range(1, currentArrangement.size() - 1).boxed().map(i -> {
            if (currentArrangement.get(i + 1) - currentArrangement.get(i - 1) <= 3) {
                List<Integer> newArrangement = new ArrayList<>(List.of(currentArrangement.get(i - 1)));
                newArrangement.addAll(currentArrangement.subList(i + 1, currentArrangement.size()));
                return getArrangements(cache, newArrangement);
            }
            return 0L;
        }).reduce(1L, Long::sum);
        cache.put(key, result);
        return result;
    }

}
