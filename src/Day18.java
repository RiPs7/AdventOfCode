import java.io.IOException;
import java.util.List;
import java.util.Stack;

class Day18 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day18.txt");
        System.out.println("The sum of all the simple evaluated results is " +
            lines.stream().map(line -> evaluate(line, false)).reduce(0L, Long::sum));
    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day18.txt");
        System.out.println("The sum of all the advanced evaluated results is " +
            lines.stream().map(line -> evaluate(line, true)).reduce(0L, Long::sum));
    }

    long evaluate(String line, final boolean isAdvanced) {
        // while there is a right parenthesis, the content needs to be evaluated first
        while (line.contains(")")) {
            // find the pair of left-right parenthesis
            int indexRightParenthesis = line.indexOf(")");
            int indexLeftParenthesis = -1;
            for (int i = indexRightParenthesis - 1; i >= 0; i--) {
                if (line.charAt(i) == '(') {
                    indexLeftParenthesis = i;
                    break;
                }
            }
            // evaluate the result
            long result = evaluate(line.substring(indexLeftParenthesis + 1, indexRightParenthesis), isAdvanced);
            // replace the parentheses with the result in the original expression
            line = line.substring(0, indexLeftParenthesis) + result + line.substring(indexRightParenthesis + 1);
        }

        // convert the infix format to postfix format
        line = infixToPostfix(line, isAdvanced);

        // a stack to push numbers and partial results
        final Stack<Long> stack = new Stack<>();

        // loop through the expression
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == ' ') {
                continue;
            }

            // if a digit is found, collect the whole number
            if (Character.isDigit(c)) {
                long n = 0;
                while (Character.isDigit(c)) {
                    n = n * 10 + (c - '0');
                    i++;
                    c = line.charAt(i);
                }
                i--;
                // and push it into the stack
                stack.push(n);
            } else {
                // if it is an operator, perform the operation with the last two numbers in the stack
                switch (c) {
                    case '+':
                        stack.push(stack.pop() + stack.pop());
                        break;
                    case '*':
                        stack.push(stack.pop() * stack.pop());
                        break;
                }
            }
        }
        // the result is the accumulated result in the stack
        return stack.pop();
    }

    String infixToPostfix(final String line, final boolean isAdvanced) {
        final StringBuilder result = new StringBuilder();
        // a stack to push non-operand characters
        final Stack<Character> stack = new Stack<>();
        for (final char c : line.toCharArray()) {
            if (isOperand(c)) { // a digit is added to the final result directly
                result.append(c);
            } else if (c == '(') { // a left parenthesis is added to the stack
                stack.push(c);
            } else if (c == ')') { // a right parenthesis designates the end of a previous left parenthesis,
                                   // so everything in the stack until a left parenthesis is added to the result
                while (!stack.isEmpty() && stack.peek() != '(') {
                    result.append(stack.pop());
                }
                // pop the remaining left parenthesis
                stack.pop();
            } else {
                // any remaining operators in the stack are to the result according to their precedence
                while (!stack.isEmpty() && (getPrecedence(c, isAdvanced) <= getPrecedence(stack.peek(), isAdvanced))) {
                    result.append(stack.pop());
                }
                stack.push(c);
            }
        }
        // add all the remaining characters from the stack to the result
        while (!stack.isEmpty()) {
            result.append(stack.pop());
        }
        return result.toString();
    }

    int getPrecedence(final char c, final boolean isAdvanced) {
        if (!isAdvanced) { // the simple precedence treats both addition and multiplication of equal priority
            switch (c) {
                case '+':
                case '*':
                default:
                    return 1;
            }
        } else {
            switch (c) { // the advance precedence treats addition of higher priority than multiplication
                case '+':
                    return 2;
                case '*':
                default:
                    return 1;
            }
        }
    }


    boolean isOperand(char c) {
        return c != '+' && c != '*';
    }
}