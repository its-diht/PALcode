import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;  // stdin field

/**
 * PAL machine Interpreter
 */
public class Interpreter {

    private static class Instr {
        private String op, arg;
        private int lineNum;

        Instr(String op, String arg, int lineNum) {
            this.op = op; 
            this.arg = arg; 
            this.lineNum = lineNum;
        }

        public String op() { return op; }
        public String arg() { return arg; }
        public int lineNum() { return lineNum; }
    }

    private final Machine machine;
    private final List<Instr> program = new ArrayList<>();
    private final Map<String,Integer> labels  = new HashMap<>();
    private final Scanner stdin   = new Scanner(System.in);

    public Interpreter(int nStacks) {
        this.machine = new Machine(nStacks);
    }

    public void load(String source) {
        String[] lines = source.split("\\r?\\n"); // honestly don't understand the regex but I'm trusting the ai that wrote it for me
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            // stripping comments
            int ci = line.indexOf(';');
            if (ci >= 0) line = line.substring(0, ci);
            line = line.strip();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String op  = parts[0].toUpperCase();
            String arg = parts.length > 1 ? parts[1].strip() : null;

            // LABELs are actually a meta instruction!(semantically I mean)
            // thus they are treated a bit differently..
            if (op.equals("LABEL")) {
                if (arg == null)
                    throw new RuntimeException("line " + (i+1) + ": no LABEL name");
                if (labels.containsKey(arg))
                    throw new RuntimeException("line " + (i+1) + ": duplicate label name '" + arg + "'");
                labels.put(arg, program.size());
            } else {
                program.add(new Instr(op, arg, i + 1));
            }
        }
    }


    public void run() {
        int pc = 0;
        while (pc < program.size()) {
            Instr ins = program.get(pc++);
            Integer jump = step(ins);
            if (jump != null) pc = jump;
        }
    }

    /** basically runs one line of instruction, returns program counter if there is a jump, null otherwise */
    private Integer step(Instr ins) {
        String op = ins.op();
        String arg = ins.arg();
        int ln = ins.lineNum();

        Integer result = null;
        switch (op) {
            // the cases are pretty much self-explanatory

            // basic operations
            case "SET":
                machine.reg = intArg(arg, ln);
                break;
            case "INCR":
                machine.reg++;
                break;
            case "DECR":
                machine.reg--;
                break;
            case "ADD":
                machine.add();
                break;
            case "SUB":
                machine.sub();
                break;
            case "MUL":
                machine.mul();
                break;
            case "DIV":
                machine.div();
                break;

            // stack stuff
            case "PUSH":
                machine.push(arg != null ? intArg(arg, ln) : machine.reg);
                break;
            case "POP":
                machine.pop();
                break;
            case "SWAP":
                machine.swap();
                break;

            // head movement
            case "MOVL":
                machine.movl();
                break;
            case "MOVR":
                machine.movr();
                break;

            // IO
            case "OUTI":
                System.out.println(machine.reg);
                break;
            case "OUTC":
                System.out.print((char) machine.reg);
                System.out.flush();
                break;
            case "INI":
                System.out.print("> ");
                System.out.flush();
                if (!stdin.hasNextInt())
                    throw new RuntimeException("line " + ln + "INI: expected an integer from stdin");
                machine.reg = stdin.nextInt();
                break;
            case "INC":
                System.out.print("> ");
                System.out.flush();
                if (!stdin.hasNext())
                    throw new RuntimeException("line " + ln + "INC: expected a character from stdin");
                machine.reg = stdin.next().charAt(0);
                break;

            // jumps(control flow)
            case "JMP":
                result = resolveLabel(strArg(arg, op, ln));
                break;
            case "JZ":
                result = machine.reg == 0 ? resolveLabel(strArg(arg, op, ln)) : null;
                break;
            case "JNZ":
                result = machine.reg != 0 ? resolveLabel(strArg(arg, op, ln)) : null;
                break;

            default:
                throw new RuntimeException("line " + ln + ": unknown instruction '" + op + "'");
        }
        return result;
    }

    private int intArg(String arg, int ln) {
        if (arg == null)
            throw new RuntimeException("line " + ln + ": missing integer argument");
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new RuntimeException("line " + ln + ": invalid integer '" + arg + "'");
        }
    }

    private String strArg(String arg, String op, int ln) {
        if (arg == null)
            throw new RuntimeException("line " + ln + ": " + op + " needs a label name");
        return arg;
    }

    private int resolveLabel(String name) {
        Integer target = labels.get(name);
        if (target == null)
            throw new RuntimeException("undefined label '" + name + "'");
        return target;
    }
}
