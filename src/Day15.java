import javafx.util.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day15 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day15.txt");
        final List<Integer> numbers = Arrays.stream(lines.get(0).split(",")).map(Integer::parseInt).collect(Collectors.toList());
        final int limit = 2020;
        final Game game = new Game(numbers, limit, false, false);
        System.out.println("The " + limit + "-th number is: " + game.play());
    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day15.txt");
        final List<Integer> numbers = Arrays.stream(lines.get(0).split(",")).map(Integer::parseInt).collect(Collectors.toList());
        final int limit = 30000000;
        final Game game = new Game(numbers, limit, false, true);
        System.out.println("The " + limit + "-th number is: " + game.play());
    }

    private static final class Game {
        final Map<Integer, Pair<Integer, Integer>> numbersAndTurnsSpoken;
        final int limit;
        int numberSpoken;
        int turn;

        boolean printSpokenNumbers;
        boolean feedback;

        public Game(final List<Integer> numbers, final int limit, final boolean printSpokenNumbers, final boolean feedback) {
            this.numbersAndTurnsSpoken = IntStream.range(0, numbers.size())
                .boxed()
                .collect(Collectors.toMap(numbers::get, i -> new Pair<>(i + 1, null)));
            this.limit = limit;
            this.printSpokenNumbers = printSpokenNumbers;
            this.feedback = feedback;
            this.turn = numbers.size();

            if (printSpokenNumbers) {
                for (int i = 1; i <= numbers.size(); i++) {
                    System.out.println(i + " -> " + numbers.get(i - 1));
                }
                System.out.println((numbers.size() + 1) + " -> " + 0);
            }
        }

        int play() {
            if (feedback) {
                final Utils.ProgressBar progressBar = new Utils.ProgressBar(limit, 50);
                while (++turn < limit) {
                    progressBar.update(this::iterate, turn);
                }
            } else {
                while (++turn < limit) {
                    iterate();
                }
            }
            return numberSpoken;
        }

        void iterate () {
            // Find if the number spoken has been spoken again and UPDATE THE TURNS in the following way:
            // 1. If the number has NOT been spoken, add a pair of the current turn and null
            // 2. If the number has been spoken ONCE, add a pair of the previous turn and the current turn
            // 3. If the number has been spoken MORE THAN ONCE, discard the old entry, and add a pair of the most
            // recent occurrence and the current turn.
            Pair<Integer, Integer> lastTwoRoundsNumberSpoken = numbersAndTurnsSpoken.get(numberSpoken);
            if (numbersAndTurnsSpoken.containsKey(numberSpoken)) {
                if (lastTwoRoundsNumberSpoken.getValue() != null) {
                    lastTwoRoundsNumberSpoken = new Pair<>(lastTwoRoundsNumberSpoken.getValue(), turn);
                } else {
                    lastTwoRoundsNumberSpoken = new Pair<>(lastTwoRoundsNumberSpoken.getKey(), turn);
                }
            } else {
                lastTwoRoundsNumberSpoken = new Pair<>(turn, null);
            }
            numbersAndTurnsSpoken.put(numberSpoken, lastTwoRoundsNumberSpoken);

            // Find the next number in the following way:
            // 1. If the spoken number has NOT been spoken, next number is 0
            // 2. If the spoken number has been spoken before, next number is the difference of current turn minus
            // the previous turn
            if (!numbersAndTurnsSpoken.containsKey(numberSpoken)) {
                numberSpoken = 0;
            } else {
                numberSpoken = turn - lastTwoRoundsNumberSpoken.getKey();
            }

            if (printSpokenNumbers) {
                System.out.println((turn + 1) + " -> " + numberSpoken);
            }
        }
    }
}