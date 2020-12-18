import javafx.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Day9 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day9.txt");
        final List<Long> numbers = lines.stream().map(Long::parseLong).collect(Collectors.toList());
        System.out.println("The invalid number is: " + crackXMAS(numbers, 25));
    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day9.txt");
        final List<Long> numbers = lines.stream().map(Long::parseLong).collect(Collectors.toList());
        long invalidNumber = crackXMAS(numbers, 25);
        final Pair<Long, Long> minMaxPair = crackXMAS2(numbers, invalidNumber);
        System.out.println("The min-max pair of the cracked region that adds up to the invalid number" +
            "(" + invalidNumber + ") is: " +
            minMaxPair + " -> " + (minMaxPair.getValue() + minMaxPair.getKey()));
    }

    long crackXMAS(final List<Long> numbers, final int windowSize) {
        for (int i = windowSize; i < numbers.size(); i++) {
            final long current = numbers.get(i);
            boolean isSumOfTwo = false;
            for (int a = i - windowSize; a < i - 1; a++) {
                final long num1 = numbers.get(a);
                for (int b = a + 1; b < i; b++) {
                    final long num2 = numbers.get(b);
                    if (num1 + num2 == current) {
                        isSumOfTwo = true;
                    }
                }
            }
            if (!isSumOfTwo) {
                return current;
            }
        }

        throw new RuntimeException("Cannot crack XMAS");
    }

    Pair<Long, Long> crackXMAS2(final List<Long> numbers, final long invalidNumber) {
        for (int i = 0; i < numbers.size() - 1; i++) {
            for (int j = i + 1; j < numbers.size(); j++) {
                final List<Long> sublist = numbers.subList(i, j);
                final long sublistSum = sublist.stream().mapToLong(Long::longValue).sum();
                if (sublistSum > invalidNumber) {
                    break;
                } else if (sublistSum == invalidNumber) {
                    return new Pair<>(
                        sublist.stream().mapToLong(Long::longValue).min().orElseThrow(NoSuchElementException::new),
                        sublist.stream().mapToLong(Long::longValue).max().orElseThrow(NoSuchElementException::new));
                }
            }
        }

        throw new RuntimeException("Cannot crack XMAS");
    }

}
