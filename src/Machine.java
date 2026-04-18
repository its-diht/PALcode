import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringJoiner;

/**
 * PAL machine state: N stacks, a movable head, and a single integer register
 */
public class Machine {

    final Deque<Integer>[] stacks;
    int head;
    int reg;

    /** 
     * machine constructor;<br>
     * pretty self-explanatory, only writing this to get rid of warnings
     */
    @SuppressWarnings("unchecked")
    public Machine(int n) {
        if (n < 1) throw new IllegalArgumentException("invalid number of stacks, must be greater than 0");
        stacks = new ArrayDeque[n];
        for (int i = 0; i < n; i++) stacks[i] = new ArrayDeque<>();
        head = 0;
        reg  = 0;
    }

    /** 
     * unused code, might use later;<br>
     * returns number of stacks in current machine
     */
    public int stacklen() { return stacks.length; } // unused, but added just in case

    private int popRaw(String op) {
        if (stacks[head].isEmpty())
            throw new RuntimeException(op + ": stack underflow at S[" + head + "]");
        return stacks[head].pop();
    }

    /** push operation(takes int) */
    public void push(int v) {
        stacks[head].push(v);
    }

    /** publically accessible pop operation */
    public int pop() {
        reg = popRaw("POP");
        return reg;
    }

    /** exchange reg with the top of the current stack */
    public void swap() {
        if (stacks[head].isEmpty())
            throw new RuntimeException("SWAP: S[" + head + "] is empty");
        int top = stacks[head].pop();
        stacks[head].push(reg);
        reg = top;
    }

    /** reg += pop(current stack) */
    public void add() { reg += popRaw("ADD"); }

    /** reg -= pop(current stack) */
    public void sub() { reg -= popRaw("SUB"); }

    /** reg *= pop(current stack) */
    public void mul() { reg *= popRaw("MUL"); }

    /** reg /= pop(current stack) */
    public void div() {
        int d = popRaw("DIV");
        if (d == 0) throw new RuntimeException("DIV by zero at S[" + head + "]");
        reg /= d;
    }

    /** checks if the currently pointed stack is empty */
    public boolean isEmpty() { return stacks[head].isEmpty(); }


    /** moves head to the left */
    public void movl() {
        if (head == 0)
            throw new RuntimeException("MOVL: already at S[0]");
        head--;
    }

    /** moves head to the right */
    public void movr() {
        if (head == stacks.length - 1)
            throw new RuntimeException("MOVR: already at S[" + (stacks.length - 1) + "]");
        head++;
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        for (int i = 0; i < stacks.length; i++)
            sj.add("S[" + i + "]=" + stacks[i]);
        return "reg=" + reg + "  head=S[" + head + "]  " + sj;
    }
}
