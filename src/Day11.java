import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Day11 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day11.txt");
        final Plane plane = new Plane(lines.stream().map(String::toCharArray).toArray(char[][]::new));
        System.out.println("At the end of simulation 1, there are " + plane.simulate(true) + " occupied seats.");
    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day11.txt");
        final Plane plane = new Plane(lines.stream().map(String::toCharArray).toArray(char[][]::new));
        System.out.println("At the end of simulation 2, there are " + plane.simulate(false) + " occupied seats.");
    }

    private static final class Plane {
        SEAT[][] layout;

        public Plane(char[][] layout) {
            this.layout = new SEAT[layout.length][layout[0].length];
            for (int i = 0; i < layout.length; i++) {
                for (int j = 0; j < layout[0].length; j++) {
                    this.layout[i][j] = SEAT.fromValue(layout[i][j]);
                }
            }
        }

        int simulate(boolean countAdjacentNeighbors) {
            boolean stateChanged = true;
            while (stateChanged) {
                stateChanged = false;
                SEAT[][] newLayout = new SEAT[layout.length][layout[0].length];
                for (int i = 0; i < layout.length; i++) {
                    for (int j = 0; j < layout[0].length; j++) {
                        final SEAT current = layout[i][j];
                        newLayout[i][j] = layout[i][j];
                        if (current == SEAT.FLOOR) {
                            continue;
                        }
                        int occupiedNeighbors = countAdjacentNeighbors ?
                            countAdjacentOccupiedNeighbors(i, j) :
                            countOccupiedNeighbors(i, j);
                        if (current == SEAT.EMPTY && occupiedNeighbors == 0) {
                            newLayout[i][j] = SEAT.OCCUPIED;
                            stateChanged = true;
                        } else if (current == SEAT.OCCUPIED && occupiedNeighbors >= (countAdjacentNeighbors ? 4 : 5)) {
                            newLayout[i][j] = SEAT.EMPTY;
                            stateChanged = true;
                        }
                    }
                }
                if (stateChanged) {
                    layout = newLayout;
                }
            }
            return Arrays.stream(layout)
                // count occupied seats per row
                .map(row -> (int) Arrays.stream(row).filter(seat -> seat == SEAT.OCCUPIED).count())
                // add up all partial counts
                .reduce(0, Integer::sum);
        }

        int countAdjacentOccupiedNeighbors(final int i, final int j) {
            int count = 0;
            // scan the 3x3 sub-grid
            for (int xOff = -1; xOff <= 1; xOff++) {
                int k = j + xOff;
                // mind left and right edges
                if (k < 0 || k > layout[0].length - 1) {
                    continue;
                }
                for (int yOff = -1; yOff <= 1; yOff++) {
                    int l = i + yOff;
                    // mind top and bottom edges
                    if (l < 0 || l > layout.length - 1) {
                        continue;
                    }
                    // skip self
                    if (xOff == 0 && yOff == 0) {
                        continue;
                    }
                    final SEAT neighbor = layout[l][k];
                    if (neighbor == SEAT.OCCUPIED) {
                        count++;
                    }
                }
            }
            return count;
        }

        int countOccupiedNeighbors(final int i, final int j) {
            final AtomicInteger count = new AtomicInteger();

            // top-left diagonal
            int x = j - 1;
            int y = i - 1;
            while (x >= 0 && y >= 0) {
                if (checkIfNotFloorAndIncrementCounter(layout[y][x], count)) {
                    break;
                }
                x--;
                y--;
            }

            // top
            x = j;
            y = i - 1;
            while (y >= 0) {
                if (checkIfNotFloorAndIncrementCounter(layout[y][x], count)) {
                    break;
                }
                y--;
            }

            // top-right diagonal
            x = j + 1;
            y = i - 1;
            while (x < layout[0].length && y >= 0) {
                if (checkIfNotFloorAndIncrementCounter(layout[y][x], count)) {
                    break;
                }
                x++;
                y--;
            }

            // left
            x = j - 1;
            y = i;
            while (x >= 0) {
                if (checkIfNotFloorAndIncrementCounter(layout[y][x], count)) {
                    break;
                }
                x--;
            }

            // right
            x = j + 1;
            y = i;
            while (x < layout[0].length) {
                if (checkIfNotFloorAndIncrementCounter(layout[y][x], count)) {
                    break;
                }
                x++;
            }

            // bottom-left diagonal
            x = j - 1;
            y = i + 1;
            while (x >= 0 && y < layout.length) {
                if (checkIfNotFloorAndIncrementCounter(layout[y][x], count)) {
                    break;
                }
                x--;
                y++;
            }

            // bottom
            x = j;
            y = i + 1;
            while (y < layout.length) {
                if (checkIfNotFloorAndIncrementCounter(layout[y][x], count)) {
                    break;
                }
                y++;
            }

            // bottom-right diagonal
            x = j + 1;
            y = i + 1;
            while (x < layout[0].length && y < layout.length) {
                if (checkIfNotFloorAndIncrementCounter(layout[y][x], count)) {
                    break;
                }
                x++;
                y++;
            }

            return count.get();
        }

        boolean checkIfNotFloorAndIncrementCounter(final SEAT seat, final AtomicInteger count) {
            if (seat == SEAT.OCCUPIED) {
                count.incrementAndGet();
                return true;
            } else {
                return seat == SEAT.EMPTY;
            }
        }

        private enum SEAT {
            OCCUPIED('#'),
            EMPTY('L'),
            FLOOR('.');

            private char type;

            SEAT(final char type) {
                this.type = type;
            }

            static SEAT fromValue(final char type) {
                return Arrays.stream(SEAT.values()).filter(v -> v.type == type).findFirst().orElse(FLOOR);
            }
        }
    }
}
