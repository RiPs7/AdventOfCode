import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day17 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day17.txt");
        final int[][] layer = lines.stream()
            .map(line -> line.chars().map(i -> i == '#' ? 1 : 0).toArray())
            .toArray(int[][]::new);
        final Engine<?, ?> engine = new Engine3(layer, 6);
        System.out.println(engine.boot());
    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day17.txt");
        final int[][][] layer = lines.stream()
            .map(line -> line.chars()
                .mapToObj(i ->
                    i == '#' ?
                        new int[]{1} :
                        new int[]{0})
                .toArray(int[][]::new))
            .toArray(int[][][]::new);
        final Engine<?, ?> engine = new Engine4(layer, 6);
        System.out.println(engine.boot());
    }

    private abstract static class Engine<DATA_TYPE, LAYER_TYPE extends Engine.Layer<DATA_TYPE>> {
        protected List<LAYER_TYPE> layers = new ArrayList<>();
        int cycles;

        abstract void cycle();

        @SuppressWarnings("unused")
        abstract List<int[]> getNeighbors(int... coords);

        int boot() {
            for (int i = 1; i <= this.cycles; i++) {
                this.cycle();
            }
            return this.count();
        }

        int count() {
            return layers.stream().map(Layer::count).reduce(0, Integer::sum);
        }

        static abstract class Layer<DATA_TYPE> {
            DATA_TYPE data;

            abstract int count();

            abstract Layer<?> expandLayer();
        }
    }

    static final class Engine3 extends Engine<int[][], Engine3.Layer3> {
        Engine3(final int[][] initialLayer, final int cycles) {
            this.cycles = cycles;
            this.layers.add(new Layer3(initialLayer));
        }

        @Override
        void cycle() {
            // expand layer
            layers = layers.stream().map(Layer::expandLayer).map(layer -> (Layer3) layer).collect(Collectors.toList());
            // add two new layers to either side of the layers list
            final int newDimX = layers.get(0).data.length;
            final int newDimY = layers.get(0).data[0].length;
            this.layers.add(0, new Layer3(newDimX, newDimY));
            this.layers.add(new Layer3(newDimX, newDimY));

            // do the cycle
            List<Layer3> newLayers = new ArrayList<>();
            for (int i = 0; i < layers.size(); i++) {
                int[][] newData = new int[layers.get(i).data.length][layers.get(i).data[0].length];
                for (int j = 0; j < layers.get(i).data.length; j++) {
                    for (int k = 0; k < layers.get(i).data[j].length; k++) {
                        int current = layers.get(i).data[j][k];
                        List<int[]> neighborsIndices = getNeighbors(i, j, k);
                        int activeNeighbors = neighborsIndices.stream().map(id -> {
                            try {
                                return layers.get(id[0]).data[id[1]][id[2]];
                            } catch (Exception e) {
                                return 0;
                            }
                        }).reduce(0, Integer::sum);
                        int newCube;
                        if (current == 0) {
                            newCube = activeNeighbors == 3 ? 1 : 0;
                        } else {
                            newCube = activeNeighbors == 2 || activeNeighbors == 3 ? 1 : 0;
                        }
                        newData[j][k] = newCube;
                    }
                }
                newLayers.add(new Layer3(newData));
            }
            layers = newLayers;
        }

        @Override
        List<int[]> getNeighbors(int... coords) {
            final int x = coords[0];
            final int y = coords[1];
            final int z = coords[2];
            List<int[]> neighbors = new ArrayList<>();
            for (int xOff = -1; xOff <= 1; xOff++) {
                for (int yOff = -1; yOff <= 1; yOff++) {
                    for (int zOff = -1; zOff <= 1; zOff++) {
                        if (xOff == 0 && yOff == 0 && zOff == 0) {
                            continue;
                        }
                        neighbors.add(new int[]{x + xOff, y + yOff, z + zOff});
                    }
                }
            }
            return neighbors;
        }

        static final class Layer3 extends Engine.Layer<int[][]> {
            Layer3(final int x, final int y) {
                this.data = new int[x][y];
                IntStream.range(0, x).boxed().forEach(i ->
                    IntStream.range(0, y).boxed().forEach(j ->
                        this.data[i][j] = 0));
            }

            Layer3(final int[][] data) {
                this.data = data;
            }

            @Override
            int count() {
                return Arrays.stream(data)
                    .map(row -> Arrays.stream(row).reduce(0, Integer::sum))
                    .reduce(0, Integer::sum);
            }

            @Override
            Layer3 expandLayer() {
                int[][] expandedData = new int[data.length + 2][data[0].length + 2];
                for (int i = 0; i < expandedData.length; i++) {
                    for (int j = 0; j < expandedData[0].length; j++) {
                        expandedData[i][j] = 0;
                    }
                }
                for (int i = 0; i < data.length; i++) {
                    System.arraycopy(data[i], 0, expandedData[i + 1], 1, data[0].length);
                }
                return new Layer3(expandedData);
            }
        }
    }

    static final class Engine4 extends Engine<int[][][], Engine4.Layer4> {
        Engine4(final int[][][] initialLayer, final int cycles) {
            this.cycles = cycles;
            this.layers.add(new Layer4(initialLayer));
        }

        @Override
        void cycle() {
            // expand layer
            layers = layers.stream().map(Layer::expandLayer).map(layer -> (Layer4) layer).collect(Collectors.toList());
            // add two new layers to either side of the layers list
            final int newDimX = layers.get(0).data.length;
            final int newDimY = layers.get(0).data[0].length;
            final int newDimZ = layers.get(0).data[0][0].length;
            this.layers.add(0, new Layer4(newDimX, newDimY, newDimZ));
            this.layers.add(new Layer4(newDimX, newDimY, newDimZ));

            // do the cycle
            List<Layer4> newLayers = new ArrayList<>();
            for (int i = 0; i < layers.size(); i++) {
                int[][][] newData = new int[layers.get(i).data.length][layers.get(i).data[0].length][layers.get(i).data[0][0].length];
                for (int j = 0; j < layers.get(i).data.length; j++) {
                    for (int k = 0; k < layers.get(i).data[j].length; k++) {
                        for (int l = 0; l < layers.get(i).data[j][k].length; l++) {
                            int current = layers.get(i).data[j][k][l];
                            List<int[]> neighborsIndices = getNeighbors(i, j, k, l);
                            int activeNeighbors = neighborsIndices.stream().map(id -> {
                                try {
                                    return layers.get(id[0]).data[id[1]][id[2]][id[3]];
                                } catch (Exception e) {
                                    return 0;
                                }
                            }).reduce(0, Integer::sum);
                            int newCube;
                            if (current == 0) {
                                newCube = activeNeighbors == 3 ? 1 : 0;
                            } else {
                                newCube = activeNeighbors == 2 || activeNeighbors == 3 ? 1 : 0;
                            }
                            newData[j][k][l] = newCube;
                        }
                    }
                }
                newLayers.add(new Layer4(newData));
            }
            layers = newLayers;
        }

        @Override
        List<int[]> getNeighbors(int... coords) {
            final int w = coords[0];
            final int x = coords[1];
            final int y = coords[2];
            final int z = coords[3];
            List<int[]> neighbors = new ArrayList<>();
            for (int wOff = -1; wOff <= 1; wOff++) {
                for (int xOff = -1; xOff <= 1; xOff++) {
                    for (int yOff = -1; yOff <= 1; yOff++) {
                        for (int zOff = -1; zOff <= 1; zOff++) {
                            if (wOff == 0 && xOff == 0 && yOff == 0 && zOff == 0) {
                                continue;
                            }
                            neighbors.add(new int[]{w + wOff, x + xOff, y + yOff, z + zOff});
                        }
                    }
                }
            }
            return neighbors;
        }

        static final class Layer4 extends Engine.Layer<int[][][]> {
            Layer4(final int x, final int y, final int z) {
                this.data = new int[x][y][z];
                IntStream.range(0, x).boxed().forEach(i ->
                    IntStream.range(0, y).boxed().forEach(j ->
                        IntStream.range(0, z).boxed().forEach(k ->
                            this.data[i][j][k] = 0)));
            }

            Layer4(final int[][][] data) {
                this.data = data;
            }

            @Override
            int count() {
                return Arrays.stream(data)
                    .map(row -> Arrays.stream(row)
                        .map(col -> Arrays.stream(col).reduce(0, Integer::sum))
                        .reduce(0, Integer::sum))
                    .reduce(0, Integer::sum);
            }

            @Override
            Layer4 expandLayer() {
                int[][][] expandedData = new int[data.length + 2][data[0].length + 2][data[0][0].length + 2];
                for (int i = 0; i < expandedData.length; i++) {
                    for (int j = 0; j < expandedData[0].length; j++) {
                        for (int k = 0; k < expandedData[0][0].length; k++) {
                            expandedData[i][j][k] = 0;
                        }
                    }
                }
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < data[0].length; j++) {
                        System.arraycopy(data[i][j], 0, expandedData[i + 1][j + 1], 1, data[0][0].length);
                    }
                }
                return new Layer4(expandedData);
            }
        }
    }
}