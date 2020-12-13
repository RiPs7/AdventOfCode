import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day3 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day3.txt");
        char[][] grid = lines.stream().map(String::toCharArray).toArray(char[][]::new);
        int trees = traverseAndCountTrees(grid, 3, 1);
        System.out.println("Encountered " + trees + " trees");
    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day3.txt");
        char[][] grid = lines.stream().map(String::toCharArray).toArray(char[][]::new);
        List<Pair<Integer, Integer>> cases = new ArrayList<>() {{
            add(new Pair<>(1, 1));
            add(new Pair<>(3, 1));
            add(new Pair<>(5, 1));
            add(new Pair<>(7, 1));
            add(new Pair<>(1, 2));
        }};
        long multipliedResults = cases.stream().map(c -> {
            long trees = traverseAndCountTrees(grid, c.getKey(), c.getValue());
            System.out.println("Encountered " + trees + " trees");
            return trees;
        }).reduce(1L, (a, b) -> a * b);
        System.out.println("Multiplication result is: " + multipliedResults);
    }

    private static int traverseAndCountTrees (char[][] grid, int dX, int dY) {
        int treesCounter = 0;
        int posX = 0;
        for (int posY = 0; posY < grid.length; posY += dY) {
            if (grid[posY][posX] == '#') {
                treesCounter++;
            }
            posX = (posX + dX) % grid[posY].length;
        }
        return treesCounter;
    }

}
