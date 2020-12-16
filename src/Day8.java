import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day8 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day8.txt");
        final Program program = new Program(lines);
        System.out.println("The value of accumulator before a command is hit for the second time is: " + program.run());

    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day8.txt");
        final Program program = new Program(lines);
        System.out.println("The value of the accumulator with the fixed instruction is: " + program.fix());
    }

    private enum COMMAND {
        NOP("nop"),
        JMP("jmp"),
        ACC("acc");

        private String command;

        COMMAND(final String command) {
            this.command = command;
        }

        private static COMMAND getCommand(String command) {
            return Arrays.stream(COMMAND.values())
                .filter(m -> m.command.equals(command))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No command enum can be found for command ': " + command + "'"));
        }
    }

    private static final class Program {
        private int ip;
        private List<Pair<COMMAND, Integer>> commands;
        private int accumulator;

        Program (final List<?> commands) {
            this.ip = 0;
            if (commands.get(0) instanceof String) {
                this.commands = commands.stream()
                    .map(com -> ((String) com).split(" "))
                    .map(spl -> new Pair<>(COMMAND.getCommand(spl[0]), Integer.parseInt(spl[1])))
                    .collect(Collectors.toList());
                this.accumulator = 0;
            } else if (commands.get(0) instanceof Pair) {
                this.commands = commands.stream()
                    .map(com -> {
                        final Pair<?, ?> commandPair = (Pair<?, ?>) com;
                        if (commandPair.getKey() instanceof COMMAND && commandPair.getValue() instanceof Integer) {
                            return new Pair<>((COMMAND) commandPair.getKey(), (Integer) commandPair.getValue());
                        }
                        throw new RuntimeException("Cannot create command from: " + commandPair.toString());
                    })
                    .collect(Collectors.toList());
            }
        }

        int run () {
            this.ip = 0;
            this.accumulator = 0;
            final Set<Integer> executedCommands = new HashSet<>();
            while (ip < commands.size()) {
                final Pair<COMMAND, Integer> currentCommand = commands.get(ip);
                final COMMAND command = currentCommand.getKey();
                if (executedCommands.contains(ip)) {
                    return accumulator;
                }
                executedCommands.add(ip);
                final int value = currentCommand.getValue();
                switch (command) {
                    case ACC:
                        accumulator += value;
                        ip++;
                        break;
                    case JMP:
                        ip += value;
                        break;
                    case NOP:
                    default:
                        ip++;
                }

            }
            return accumulator;
        }

        boolean isTerminated () {
            return ip == commands.size();
        }

        int fix () {
            for (int i = 0; i < commands.size(); i++) {
                final Pair<COMMAND, Integer> currentCommand = commands.get(i);
                final COMMAND command = currentCommand.getKey();
                if (command == COMMAND.ACC) {
                    continue;
                }
                // Get the new command
                final List<Pair<COMMAND, Integer>> newCommands = new ArrayList<>(commands);
                // Change the command
                final COMMAND newCommand = command == COMMAND.NOP ? COMMAND.JMP : COMMAND.NOP;
                // Create the new command set
                final Pair<COMMAND, Integer> newFullCommand = new Pair<>(newCommand, currentCommand.getValue());
                newCommands.set(i, newFullCommand);
                // Create the program with the new command set
                final Program newProgram = new Program(newCommands);
                // and run it
                newProgram.run();
                // if it terminates, return the accumulator
                if (newProgram.isTerminated()) {
                    return newProgram.accumulator;
                }
            }
            throw new RuntimeException("Cannot fix this program");
        }
    }
}
