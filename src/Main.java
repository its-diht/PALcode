import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * PAL — Pushdown Automata Language
 * <br><br>
 * Compile: <br>
 *   `javac src/*.java -d out`
 * <br><br>
 * Run a .pal file (default 2 stacks): <br>
 *   `java -cp out Main [file.pal] [nStacks]`
 */
public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String file    = args[0];
        int    nStacks = 2;
        if (args.length > 1) {
            try {
                nStacks = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Error: nStacks must be an integer, got '" + args[1] + "'");
                printUsage();
                return;
            }
        }
        String source = new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);
        run(source, nStacks);
    }

    private static void printUsage() {
        System.out.println("PAL — Pushdown Automata Language");
        System.out.println("  java -cp out Main <file.pal> [nStacks=2]");
    }

    private static void run(String source, int n) {
        Interpreter interp = new Interpreter(n);
        interp.load(source);
        interp.run();
    }
}
