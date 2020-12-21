import javafx.util.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day13 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day13.txt");
        final long timestamp = Long.parseLong(lines.get(0));
        final List<Long> ids = Arrays.stream(lines.get(1).split(","))
            .filter(s -> !s.equals("x"))
            .map(Long::parseLong)
            .sorted()
            .collect(Collectors.toList());
        final Map<Long, Long> idsWithFirstMultipleAfterTimestamp = ids.stream().map(id -> {
            for (long i = timestamp; i < timestamp + id; i++) {
                if (i % id == 0) {
                    return new Pair<>(id, i - timestamp);
                }
            }
            throw new RuntimeException("Error for id " + id);
        }).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        final Map.Entry<Long, Long> closestIdWithFirstMultipleAfterTimestamp =
            Collections.min(idsWithFirstMultipleAfterTimestamp.entrySet(), Map.Entry.comparingByValue());

        final long earliestId = closestIdWithFirstMultipleAfterTimestamp.getKey();
        final long minutesToWait = closestIdWithFirstMultipleAfterTimestamp.getValue();
        System.out.println(
            "Earliest bus ID is " + earliestId + " and the waiting time is " + minutesToWait + " --> " + earliestId * minutesToWait);
    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day13.txt");
        final List<Long> ids = Arrays.stream(lines.get(1).split(","))
            .map(id -> id.equals("x") ? 0 : Long.parseLong(id))
            .collect(Collectors.toList());

        final AtomicInteger congruenceSystemIndex = new AtomicInteger(-1);
        final Map<Integer, Pair<Long, Long>> congruenceSystem = IntStream.range(0, ids.size())
            .boxed()
            .filter(i -> ids.get(i) != 0)
            .collect(Collectors.toMap(
                i -> congruenceSystemIndex.incrementAndGet(),
                i -> new Pair<>(i == 0 ? 0 : ids.get(i) - (i % ids.get(i)), ids.get(i))));

        long solution = applyChineseRemainderTheorem(congruenceSystem);
        System.out.println("The earliest timestamp such that all of the listed bus IDs depart at offsets matching their positions in the list is: " + solution);
    }

    /**
     * Applies the Chinese Remainder Theorem on a Congruence System, given in the form of a {@link Map}.
     * The map is keyed by the index of a congruence (i.e. 0, 1, 2, ...).
     * The map values are given in the form of {@link Pair}s. Every {@link Pair} has a key of the modulo, and a value
     * of the remainder.
     * Example:
     * The following Congruence System:
     * X ≡ rem[0] (mod mod[0])
     * X ≡ rem[1] (mod mod[1])
     * X ≡ rem[2] (mod mod[2])
     * ...
     * will be given in the following map
     * 0 -> Pair(rem[0], mod[0])
     * 1 -> Pair(rem[1], mod[1])
     * 2 -> Pair(rem[2], mod[2])
     *
     * @param congruenceSystem
     *  The map of the congruence system
     * @return
     *  The solution to the CRM
     */
    long applyChineseRemainderTheorem(final Map<Integer, Pair<Long, Long>> congruenceSystem) {
        // Check that the Chinese Remainder Theorem can be applied (co-primality test for the keys)
        final List<Long> mod = congruenceSystem.values().stream().map(Pair::getValue).collect(Collectors.toList());
        final List<Pair<Long, Long>> modPairs = IntStream.range(0, mod.size() - 1)
            .boxed()
            .map(i -> IntStream.range(i + 1, mod.size())
                .boxed()
                .map(j -> new Pair<>(mod.get(i), mod.get(j)))
                .collect(Collectors.toList()))
            .flatMap(List::stream)
            .collect(Collectors.toList());
        if (modPairs.stream().map(pair -> findGCD(pair.getKey(), pair.getValue())).anyMatch(gcd -> gcd != 1)) {
            throw new RuntimeException("Cannot apply Chinese Remainder Theorem as the numbers are not all co-prime");
        }

        // Apply Chinese Remainder Theorem to solve the congruence system
        // 1. Calculate N as the product of all mod
        final long NProduct = mod.stream().reduce(1L, (a, b) -> a * b);
        // 2. Calculate all N[i]s as the product of all mod, but the i-th one - Divide N by the i-th mod.
        final Long[] N = IntStream.range(0, mod.size())
            .boxed()
            .map(i -> NProduct / mod.get(i))
            .toArray(Long[]::new);
        // 3. Calculate all X[i]s such that N[i] * X[i] = congruenceSystem.value.key (mod congruenceSystem.value.value).
        final Long[] X = IntStream.range(0, mod.size())
            .boxed()
            .map(i -> {
                final Pair<Long, Long> pair = congruenceSystem.get(i);
                final Long modulo = pair.getValue();
                if (modulo == 0) {
                    return N[i];
                } else {
                    return findInverseModulo(N[i], modulo);
                }
            })
            .toArray(Long[]::new);
        // 4. Calculate the sum Σ(N[i] * X[i] * mod[i]).
        final Long sum = IntStream.range(0, mod.size())
            .boxed()
            .map(i -> N[i] * X[i] * congruenceSystem.get(i).getKey())
            .reduce(0L, Long::sum);

        return sum % NProduct;
    }

    /**
     * Finds the GCD of two integers a and b.
     *
     * @param a
     *  The first integer
     * @param b
     *  The second integer
     * @return
     *  The GCD
     */
    long findGCD(final long a, final long b) {
        return b == 0 ? a : findGCD(b, a % b);
    }

    /**
     * Finds the modular multiplicative inverse of a number {@code num} modulo {@code mod}.
     *
     * @param num
     *  The number to find the modular multiplicative inverse
     * @param mod
     *  The modulo
     * @return
     *  The modular multiplicative inverse
     */
    long findInverseModulo(long num, long mod){
        long originalMod = mod;
        long temp;
        long quotient;
        long x = 0;
        long result = 1;

        if (mod == 1)
            return 0;

        while (num > 1)
        {
            quotient = num / mod;
            temp = mod;

            mod = num % mod;
            num = temp;
            temp = x;
            x = result - quotient * x;
            result = temp;
        }

        if (result < 0)
            result += originalMod;

        return result;
    }
}
