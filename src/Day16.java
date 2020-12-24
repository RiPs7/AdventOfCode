import javafx.util.Pair;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day16 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day16.txt");
        final List<Rule> rules = lines.stream()
            .filter(line -> line.matches(".+:.+or.+"))
            .map(Rule::fromString)
            .collect(Collectors.toList());
        final Ticket myTicket = Ticket.fromString(lines.get(IntStream.range(0, lines.size())
            .filter(i -> "your ticket:".equals(lines.get(i)))
            .findFirst().orElseThrow(() -> new RuntimeException("Cannot find your ticket")) + 1));
        final List<Ticket> tickets = lines.stream()
            .filter(line -> line.matches("(.+,)+.+"))
            .map(Ticket::fromString)
            .filter(ticket -> !ticket.equals(myTicket))
            .collect(Collectors.toList());

        System.out.println("The ticket scanning error rate is: " +
            tickets.stream().map(ticket -> ticket.validate(rules)).reduce(0, Integer::sum));
    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day16.txt");
        final List<Rule> rules = lines.stream()
            .filter(line -> line.matches(".+:.+or.+"))
            .map(Rule::fromString)
            .collect(Collectors.toList());
        final Ticket myTicket = Ticket.fromString(lines.get(IntStream.range(0, lines.size())
            .filter(i -> "your ticket:".equals(lines.get(i)))
            .findFirst().orElseThrow(() -> new RuntimeException("Cannot find your ticket")) + 1));
        final List<Ticket> tickets = lines.stream()
            .filter(line -> line.matches("(.+,)+.+"))
            .map(Ticket::fromString)
            .filter(ticket -> !ticket.equals(myTicket))
            .collect(Collectors.toList());
        final List<Ticket> validTickets = tickets.stream()
            .filter(ticket -> ticket.validate(rules) == 0)
            .collect(Collectors.toList());

        // collect all the candidate indices for every rule
        final Map<Rule, List<Integer>> possibleCandidatesForRules = rules.stream()
            .collect(Collectors.toMap(
                Function.identity(),
                rule -> IntStream.range(0, myTicket.values.size()).boxed()
                    .filter(i -> validTickets.stream()
                        .filter(ticket -> rule.validate(ticket.values.get(i))).count() == validTickets.size())
                    .collect(Collectors.toList())));

        // collect all the rules mapped to their actual order (index)
        final Map<Rule, Integer> rulesOrder = new HashMap<>();

        while (!possibleCandidatesForRules.isEmpty()) {
            // get the rule that matches only with one candidate
            final Map.Entry<Rule, Integer> ruleWithOneCandidate = possibleCandidatesForRules.entrySet().stream()
                .filter(entry -> entry.getValue().size() == 1)
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().get(0)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Can't find a rule that matches exactly one field"));

            // add that rule with the single candidate to the final values
            rulesOrder.put(ruleWithOneCandidate.getKey(), ruleWithOneCandidate.getValue());

            // remove the candidate from all the other rules that contain it as a candidate
            possibleCandidatesForRules.entrySet().stream()
                .filter(entry -> entry.getValue().contains(ruleWithOneCandidate.getValue()))
                .forEach(entry -> entry.getValue().remove(ruleWithOneCandidate.getValue()));

            // remove the entry with the single candidate rule from the map completely
            possibleCandidatesForRules.remove(ruleWithOneCandidate.getKey());
        }

        // collect all the rule names to the ticket field value
        final Map<String, Integer> valuesFromDecodedTicket = rulesOrder.entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getKey().name,entry -> myTicket.values.get(entry.getValue())));

        // create the decoded ticket
        final DecodedTicket myDecodedTicket = new DecodedTicket(valuesFromDecodedTicket);

        System.out.println("The product of the values for the fields that start with 'departure' is: " +
            myDecodedTicket.values.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("departure"))
                .map(Map.Entry::getValue)
                .map(i -> (long) i).reduce(1L, (a, b) -> a * b));
    }

    private static final class Ticket {
        List<Integer> values;

        public Ticket(List<Integer> values) {
            this.values = values;
        }

        static Ticket fromString(final String str) {
            return new Ticket(Arrays.stream(str.split(",")).map(Integer::parseInt).collect(Collectors.toList()));
        }

        /**
         * Validates a ticket against a list of {@link Rule}s.
         * The validation is an integer of the sum of the invalid values. A result value of 0 indicates that the ticket
         * is valid. Any other positive value indicates that the ticket is invalid.
         *
         * @param rules The list of rules
         * @return The validation integer
         */
        int validate(final List<Rule> rules) {
            return values.stream()
                // for every ticket value, construct a pair with
                .map(value -> new Pair<>(
                    // a value of the ticket value
                    value,
                    // and a key of the number of rules that
                    rules.stream()
                        // their fields satisfy the value (i.e. min >= value >= max)
                        .filter(rule -> rule.validate(value))
                        .count()))
                // and select only the pairs where no rules are satisfied
                .filter(pair -> pair.getValue() == 0)
                // get only the key (the initial ticket value)
                .map(Pair::getKey)
                // and sum them all up.
                .reduce(0, Integer::sum);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Ticket)) {
                return false;
            }
            Ticket other = (Ticket) obj;
            return this.values.equals(other.values);
        }
    }

    private static final class DecodedTicket {
        Map<String, Integer> values;

        public DecodedTicket(final Map<String, Integer> values) {
            this.values = values;
        }

    }

    private static final class Rule {
        String name;
        List<Field> fields;

        public Rule(final String name, final List<Field> fields) {
            this.name = name;
            this.fields = fields;
        }

        static Rule fromString(final String str) {
            final String name = str.substring(0, str.indexOf(':'));
            return new Rule(
                name,
                Arrays.stream(str.replace(name + ": ", "").split(" or "))
                    .map(Field::fromString)
                    .collect(Collectors.toList()));
        }

        boolean validate(int value) {
            return this.fields.stream().anyMatch(field -> value >= field.min && value <= field.max);
        }

        private static final class Field {
            int min;
            int max;

            public Field(final int min, final int max) {
                this.min = min;
                this.max = max;
            }

            static Field fromString(final String str) {
                final String[] parts = str.split("-");
                return new Field(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            }
        }
    }
}