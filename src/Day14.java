import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day14 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day14.txt");
        final Program1 program = new Program1(lines);
        program.run();
        long sum = Arrays.stream(program.memory).boxed().reduce(0L, Long::sum);
        System.out.println("The sum of all values left in memory after the program (v1) completes is: " + sum);
    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day14.txt");
        final Program2 program = new Program2(lines);
        program.run();
        long sum = program.memory.values().stream().reduce(0L, Long::sum);
        System.out.println("The sum of all values left in memory after the program (v2) completes is: " + sum);
    }

    private static final class Program1 {
        private static final Pattern maskPattern = Pattern.compile("mask = (?<value>.+)");
        private static final Pattern memPattern = Pattern.compile("mem\\[(?<addr>\\d+)] = (?<value>\\d+)");

        final List<Command> commands;
        final long[] memory;
        String mask;

        public Program1(final List<String> lines) {
            commands = lines.stream().map(line -> {
                final Matcher maskMatcher = maskPattern.matcher(line);
                final Matcher memMatcher = memPattern.matcher(line);
                if (maskMatcher.matches()) {
                    final String maskValue = maskMatcher.group("value");
                    return new MaskCommand(maskValue);
                } else if (memMatcher.matches()) {
                    final long memAddress = Long.parseLong(memMatcher.group("addr"));
                    final long memValue = Long.parseLong(memMatcher.group("value"));
                    return new MemCommand(memAddress, memValue);
                } else {
                    throw new RuntimeException("Unrecognized command in line: " + line);
                }
            }).collect(Collectors.toList());
            final long memorySize = commands.stream()
                .filter(comm -> comm instanceof MemCommand)
                .map(comm -> (MemCommand) comm)
                .max(Comparator.comparingLong(memComm -> memComm.address))
                .orElseThrow(() -> new RuntimeException("Can't determine memory size")).address;
            memory = new long[(int) memorySize];
        }

        void run() {
            commands.forEach(command -> command.run(this));
        }

        private static abstract class Command {
            abstract void run(Program1 program);
        }

        private static final class MaskCommand extends Command {
            String value;

            public MaskCommand(String value) {
                this.value = value;
            }

            @Override
            void run(final Program1 program) {
                program.mask = value;
            }
        }

        private static final class MemCommand extends Command {
            long address;
            long value;

            public MemCommand(long address, long value) {
                this.address = address;
                this.value = value;
            }

            @Override
            void run(final Program1 program) {
                final String binaryValue = String.format("%36s", Long.toString(value, 2)).replace(' ', '0');
                final String mask = program.mask;
                final StringBuilder result = new StringBuilder();
                for (int i = 0; i < mask.length(); i++) {
                    char maskChar = mask.charAt(i);
                    if (maskChar == 'X') {
                        result.append(binaryValue.charAt(i));
                    } else {
                        result.append(maskChar);
                    }
                }
                final long decimalResult = Long.parseLong(result.toString(), 2);
                program.memory[(int) address - 1] = decimalResult;
            }
        }
    }

    private static final class Program2 {
        private static final Pattern maskPattern = Pattern.compile("mask = (?<value>.+)");
        private static final Pattern memPattern = Pattern.compile("mem\\[(?<addr>\\d+)] = (?<value>\\d+)");

        final List<Command> commands;
        final Map<Long, Long> memory;
        String mask;

        public Program2(final List<String> lines) {
            commands = lines.stream().map(line -> {
                final Matcher maskMatcher = maskPattern.matcher(line);
                final Matcher memMatcher = memPattern.matcher(line);
                if (maskMatcher.matches()) {
                    final String maskValue = maskMatcher.group("value");
                    return new MaskCommand(maskValue);
                } else if (memMatcher.matches()) {
                    final long memAddress = Long.parseLong(memMatcher.group("addr"));
                    final long memValue = Long.parseLong(memMatcher.group("value"));
                    return new MemCommand(memAddress, memValue);
                } else {
                    throw new RuntimeException("Unrecognized command in line: " + line);
                }
            }).collect(Collectors.toList());
            memory = new HashMap<>();
        }

        void run() {
            commands.forEach(command -> command.run(this));
        }

        private static abstract class Command {
            abstract void run(Program2 program);
        }

        private static final class MaskCommand extends Command {
            String value;

            public MaskCommand(String value) {
                this.value = value;
            }

            @Override
            void run(final Program2 program) {
                program.mask = value;
            }
        }

        private static final class MemCommand extends Command {
            long address;
            long value;

            public MemCommand(long address, long value) {
                this.address = address;
                this.value = value;
            }

            @Override
            void run(final Program2 program) {
                final String binaryAddress = String.format("%36s", Long.toString(address, 2)).replace(' ', '0');
                final String mask = program.mask;
                final StringBuilder result = new StringBuilder();
                for (int i = 0; i < mask.length(); i++) {
                    char maskChar = mask.charAt(i);
                    if (maskChar == '0') {
                        result.append(binaryAddress.charAt(i));
                    } else if (maskChar == '1') {
                        result.append('1');
                    } else if (maskChar == 'X') {
                        result.append('X');
                    }
                }
                final Long[] decimalResults = populateAllCombinations(result.toString());
                for (final long decimalAddress : decimalResults) {
                    program.memory.put(decimalAddress - 1, value);
                }

            }

            static Long[] populateAllCombinations(final String value) {
                final int countX = (int) value.chars().boxed().filter(c -> c == 'X').count();
                return IntStream.range(0, (int) Math.pow(2, countX))
                    .boxed()
                    .map(number -> {
                        final String binary = String.format("%" + countX + "s", Integer.toString(number, 2)).replace(' ', '0');
                        final StringBuilder sb = new StringBuilder();
                        int index = 0;
                        for (int i = 0; i < value.length(); i++) {
                            if (value.charAt(i) == 'X') {
                                sb.append(binary.charAt(index++));
                            } else {
                                sb.append(value.charAt(i));
                            }
                        }
                        return Long.parseLong(sb.toString(), 2);
                    })
                    .toArray(Long[]::new);
            }
        }
    }
}