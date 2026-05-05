import java.util.List;
import java.util.Scanner;

// PART 5 - Trace Output and Entry Point
// Reads an expression and prints the tokens, parse result, tree, and answer.
public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Mini Expression Compiler (type 'quit' to exit)");

        while (true) {
            System.out.print("Expression: ");
            String line = input.nextLine().trim();
            if (line.equals("quit")) break;
            if (line.equals("")) continue;

            run(line);
            System.out.println();
        }
        input.close();
    }

    public static void run(String expr) {
        // 1) Tokenize
        List<Lexer.Token> tokens;
        try {
            tokens = Lexer.tokenize(expr);
        } catch (RuntimeException e) {
            System.out.println("Lexer error: " + e.getMessage());
            return;
        }
        System.out.println("Tokens: " + tokens);

        // 2) Parse (validate grammar)
        Parser parser = new Parser(tokens);
        if (!parser.parse()) {
            System.out.println("Parse Result: FAILURE");
            System.out.println("Error: " + parser.error);
            return;
        }
        System.out.println("Parse Result: SUCCESS");

        // 3) Build the tree
        ASTBuilder builder = new ASTBuilder(tokens);
        ASTBuilder.Node tree = builder.build();

        // 4) Print the tree
        System.out.println("Parse Tree:");
        printTree(tree, 0);

        // 5) Evaluate
        try {
            int result = Evaluator.evaluate(tree);
            System.out.println("Evaluation Result: " + result);
        } catch (RuntimeException e) {
            System.out.println("Evaluation error: " + e.getMessage());
        }
    }

    private static void printTree(ASTBuilder.Node root, int depth) {
        if (root == null) return;
        printVertical(root);
    }

// ---------------- VERTICAL TREE PRINTER ----------------

    private static class Line {
        String text;
        Line(String t) { text = t; }
    }

    private static void printVertical(ASTBuilder.Node node) {
        for (String s : buildLines(node)) {
            System.out.println(s);
        }
    }

    private static java.util.List<String> buildLines(ASTBuilder.Node node) {
        if (node == null) return java.util.List.of();

        String val = node.value;

        // leaf node
        if (node.left == null && node.right == null) {
            return java.util.List.of(val);
        }

        java.util.List<String> left  = buildLines(node.left);
        java.util.List<String> right = buildLines(node.right);

        int leftWidth  = left.isEmpty()  ? 0 : left.get(0).length();
        int rightWidth = right.isEmpty() ? 0 : right.get(0).length();

        int rootPos = leftWidth;
        int totalWidth = leftWidth + val.length() + rightWidth;

        // root line
        String rootLine = " ".repeat(rootPos) + val;

        // branch line
        String branchLine = "";
        if (node.left != null) {
            branchLine += " ".repeat(rootPos - 1) + "/";
        } else {
            branchLine += " ".repeat(rootPos);
        }

        if (node.right != null) {
            branchLine += " ".repeat(val.length()) + "\\";
        }

        // merge children line-by-line
        java.util.List<String> merged = new java.util.ArrayList<>();
        merged.add(rootLine);
        merged.add(branchLine);

        int max = Math.max(left.size(), right.size());
        for (int i = 0; i < max; i++) {
            String L = i < left.size()  ? left.get(i)  : " ".repeat(leftWidth);
            String R = i < right.size() ? right.get(i) : " ".repeat(rightWidth);
            merged.add(L + " ".repeat(val.length()) + R);
        }

        return merged;
    }

}
