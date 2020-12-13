
public class Main {

    /**
     * The structure of this application is as follows:
     * <ol>
     * <li>This {@code main} method loops over all the values in the {@link DaysEnum}, contains all the implemented days
     * of the Advent Of Code, and a reference to each of implementation {@code Day} classes.</li>
     * <li>Each enum from the above {@link DaysEnum} calls the {@link DaysEnum#run()} method.</li>
     * <li>This triggers the {@link Day#part1()} and {@link Day#part2()} methods, which will execute Part 1 and Part 2
     * from each Day.</li>
     * <li>All the implementing {@code DayX} classes need to extend the abstract {@link Day} class, which will be the
     * entry point of invocation of the solid methods in each {@code Day}</li>
     * <li>The output from Part 1 and Part 2 of all the days appears in the console</li>
     * </ol>
     * The abstraction of the above structure can be briefly seen in the diagram below:
     * <pre>
     *        main
     *          |
     *      DaysEnum
     *     /    |   \
     *    /     |    \
     *  DAY1   DAY2  ...
     *   |      |     |
     *  run    run   run
     *    \     |     /
     *     \    |    /
     *      +-------+
     *          |
     *        part1
     *          +
     *        part2
     *       /  |  \
     *      /   |   \
     *   Day1  Day2 ...
     *     \    |    /
     *      \   |   /
     *       +-----+
     *          |
     *        part1
     *          +
     *        part2
     * </pre>
     * @param args
     *  Default args to the main method
     */
    public static void main(String[] args) {
        for (DaysEnum day : DaysEnum.values()) {
            System.out.println("----- Running for " + day.name() + " -----");
            day.run();
            System.out.println("----------------------------");
        }
    }

}
