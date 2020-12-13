import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Day6 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day6.txt");
        final List<String> allGroupAnswers = extractGroupAnswers1(lines);
        final List<Integer> allGroupIndividualAnswers = allGroupAnswers.stream().map(Day6::countGroupIndividualAnswers).collect(Collectors.toList());
        System.out.println("The sum of the counts of all group individual answers is: " + allGroupIndividualAnswers.stream().reduce(0, Integer::sum));
    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day6.txt");
        List<List<String>> allGroupAnswers = extractGroupAnswers2(lines);
        List<Integer> allGroupMatchingAnswers = allGroupAnswers.stream().map(Day6::countGroupMatchingAnswers).collect(Collectors.toList());
        System.out.println("The sum of the counts of all group matching answers is: " + allGroupMatchingAnswers.stream().reduce(0, Integer::sum));
    }

    private static List<String> extractGroupAnswers1(List<String> lines) {
        final List<String> allGroupAnswersLines = new ArrayList<>();
        StringBuilder groupAnswersLines = new StringBuilder();
        for (final String currentLine : lines) {
            if (isNotBlank(currentLine)) {
                groupAnswersLines.append(currentLine);
            } else {
                allGroupAnswersLines.add(groupAnswersLines.toString().trim());
                groupAnswersLines = new StringBuilder();
            }
        }
        allGroupAnswersLines.add(groupAnswersLines.toString());
        return allGroupAnswersLines;
    }

    private static int countGroupIndividualAnswers(final String s) {
        final HashSet<Character> set = new HashSet<>();
        for (char c : s.toCharArray()) {
            set.add(c);
        }
        return set.size();
    }

    private static boolean isNotBlank(final String s) {
        return s != null && !s.equals("");
    }

    private static List<List<String>> extractGroupAnswers2(List<String> lines) {
        final List<List<String>> allGroupAnswersLines = new ArrayList<>();
        List<String> groupAnswersLines = new ArrayList<>();
        for (final String currentLine : lines) {
            if (isNotBlank(currentLine)) {
                groupAnswersLines.add(currentLine);
            } else {
                allGroupAnswersLines.add(groupAnswersLines);
                groupAnswersLines = new ArrayList<>();
            }
        }
        allGroupAnswersLines.add(groupAnswersLines);
        return allGroupAnswersLines;
    }

    private static int countGroupMatchingAnswers(final List<String> answers) {
        int matchingCount = 0;
        for (char c : answers.get(0).toCharArray()) {
            boolean isMatching = true;
            for (String answer : answers) {
                if (!answer.contains(c + "")) {
                    isMatching = false;
                    break;
                }
            }
            if (isMatching) {
                matchingCount++;
            }
        }
        return matchingCount;
    }

}
