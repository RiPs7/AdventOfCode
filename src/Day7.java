import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day7.txt");
        final Map<Bag, Map<Bag, Integer>> rules = lines.stream()
            .map(Day7::extractRule)
            .collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll);
        final String myBag = "shiny gold";
        int numberOFWays = countNumberOFWays(new Bag(myBag), rules);
        System.out.println("The number of bags that can eventually contain a '" + myBag + "' bag is: " + numberOFWays);
    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day7.txt");
        final Map<Bag, Map<Bag, Integer>> rules = lines.stream()
            .map(Day7::extractRule)
            .collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll);
        final String myBag = "shiny gold";
        long numberOfBags = countNumberOFTotalBags(new Bag(myBag), rules);
        System.out.println("A '" + myBag + "' bag must contain " + numberOfBags + " number of bags");
    }

    private static Pair<Bag, Map<Bag, Integer>> extractRule(String line) {
        final String[] parts = line.replaceAll("\\.", "").split("\\s*contain\\s*");
        final Bag from = new Bag(parts[0].replaceAll("\\s*bags?\\s*", "").trim());
        try {
            final Map<Bag, Integer> to = Stream.of(parts[1].split("\\s*,\\s*"))
                .map(p -> p.replaceAll("\\s*bags?\\s*", "").trim())
                .map(p -> p.split(" ", 2))
                .map(p -> new Pair<>(new Bag(p[1]), Integer.parseInt(p[0])))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
            return new Pair<>(from, to);
        } catch (NumberFormatException nfe) {
            return new Pair<>(from, null);
        }
    }

    private static int countNumberOFWays(final Bag target, final Map<Bag, Map<Bag, Integer>> rules) {
        int numberOfWays = 0;
        // Apply a DFS from every possible beginning to the target and check if the target can be reached
        for (final Map.Entry<Bag, Map<Bag, Integer>> rule : rules.entrySet()) {
            if (rule.getKey().equals(target)) {
                continue;
            }
            if (DFSToTarget(rules, rule.getKey(), target)) {
                numberOfWays++;
            }
        }
        return numberOfWays;
    }

    private static boolean DFSToTarget(final Map<Bag, Map<Bag, Integer>> rules, final Bag start, final Bag target) {
        // The closed set, for the items that have been checked
        final Set<Bag> closed = new HashSet<>();
        // The frontier stack, for the items that have been added for checks
        final Stack<Bag> frontier = new Stack<>();
        // Push the starting item in the frontier
        frontier.push(start);
        // While the frontier is not empty...
        while (!frontier.isEmpty()) {
            // Pop the top item
            final Bag current = frontier.pop();
            // If we have reached the target, return true
            if (current.equals(target)) {
                return true;
            }
            // If the closed set contains the current item, then go to the next one
            if (closed.contains(current)) {
                continue;
            }
            // Otherwise, add the current item to the closed set
            closed.add(current);
            // Find the rule for the current item
            final Map.Entry<Bag, Map<Bag, Integer>> ruleForCurrent = rules.entrySet().stream()
                .filter(r -> r.getKey().equals(current))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Can't find rule for: " + current));
            // If there are no children items, then go to the next one
            if (ruleForCurrent.getValue() == null) {
                continue;
            }
            // Get all the children bags from the current rule
            final List<Bag> childrenBags = new ArrayList<>(ruleForCurrent.getValue().keySet());
            // Add all the children items to the frontier
            for (final Bag toBag : childrenBags) {
                frontier.push(toBag);
            }
        }
        return false;
    }

    private static int countNumberOFTotalBags(final Bag start, Map<Bag, Map<Bag, Integer>> rules) {
        // A recursive approach is followed:
        // Find the rule for the start item
        final Map.Entry<Bag, Map<Bag, Integer>> ruleForCurrent = rules.entrySet().stream()
            .filter(r -> r.getKey().equals(start))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Can't find rule for: " + start));
        // Get the value of the current rule (the possible bags it can contain)
        final Map<Bag, Integer> value = ruleForCurrent.getValue();
        // if it can't contain any bags, return 0
        if (value == null) {
            return 0;
        } else {
            // else recursively count the number of bags for every bag the current one can contain
            return value.entrySet().stream()
                .reduce(
                    0,
                    // Adds to the previous accumulation result the number of the bags the current one contains
                    // multiplied by the number of bags each one of them contains.
                    (a, b) -> a + b.getValue() * (1 + countNumberOFTotalBags(b.getKey(), rules)),
                    Integer::sum);
        }
    }

    private static final class Bag {
        String type;

        public Bag(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.type);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Bag)) {
                return false;
            }
            Bag other = (Bag) obj;
            return this.type.equals(other.type);
        }
    }

}
