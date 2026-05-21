package ai.betterme;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

/**
 * Day 12 — Balanced Brackets (stack-based bracket matcher).
 *
 * <p>The classic interview kata: given a string containing the bracket
 * characters {@code () [] { }} (and possibly any other characters, which must
 * be ignored), decide whether every opener has a matching closer in the
 * correct order. Examples:
 *
 * <pre>
 *   "()"            ->  true
 *   "()[]{}"        ->  true
 *   "([{}])"        ->  true
 *   "(]"            ->  false   (mismatched pair)
 *   "([)]"          ->  false   (interleaved, not nested)
 *   "("             ->  false   (opener never closed)
 *   ")"             ->  false   (closer with nothing open)
 *   ""              ->  true    (vacuously balanced)
 *   "a (b + c) * d" ->  true    (ignore non-bracket chars)
 *   null            ->  IllegalArgumentException
 * </pre>
 *
 * <h2>Why this is a "stack" problem</h2>
 *
 * Brackets nest. When you see an opener, you don't yet know which closer
 * matches it — you only find out later, when its closer appears. The most
 * recent unmatched opener is the one that has to close next. "Most recent
 * unmatched" is exactly LIFO order, which is exactly what a stack gives you.
 *
 * <h2>Idiomatic Java vocabulary you should reach for</h2>
 *
 * <ul>
 *   <li><b>{@link java.util.ArrayDeque}</b> as the stack. Same data structure
 *       you used for the iterative DFS on Day 9. NOT {@code java.util.Stack}
 *       (legacy, synchronized) and NOT {@code java.util.LinkedList} (fat,
 *       node-per-element).</li>
 *   <li><b>A {@code Map<Character, Character>} lookup table</b> mapping each
 *       <i>closer</i> to its matching <i>opener</i>. This is the
 *       data-driven idiom: the table <i>is</i> the rules of the game, so
 *       there are no {@code if/else} chains comparing characters. (This is
 *       exactly the lesson from Day 5b Roman numerals — let the data drive
 *       the algorithm.)</li>
 *   <li><b>{@link IllegalArgumentException}</b> for the {@code null} input
 *       case — not {@code UnsupportedOperationException}, not {@code
 *       NullPointerException}. Same convention you used on Days 7, 8, 10, 11.</li>
 * </ul>
 *
 * <h2>Complexity target</h2>
 *
 * <p>O(n) time, O(n) auxiliary space (worst case: a string of n openers).
 */
public final class BalancedBrackets {

    /**
     * The single piece of state the algorithm needs: which closer matches
     * which opener. Declared once, as immutable data, at the top of the file.
     *
     * <p><b>STEP 0 (read this before you write any code):</b> Notice the
     * direction — keys are <i>closers</i>, values are <i>openers</i>. That
     * is deliberate. When you read a closer from the input, you look it up
     * here to find what the matching opener <i>should have been</i>, then
     * compare that against the top of your stack. If you keyed by opener
     * instead, you'd be looking up the wrong direction at the wrong moment.
     */
    private static final Map<Character, Character> CLOSER_TO_OPENER = Map.of(
            ')', '(',
            ']', '[',
            '}', '{');

    private BalancedBrackets() {
        // utility class — no instances
    }

    /**
     * Returns {@code true} iff every bracket in {@code input} is properly
     * nested and matched. Non-bracket characters are ignored.
     *
     * @throws IllegalArgumentException if {@code input} is {@code null}.
     */
    public static boolean isBalanced(String input) {

        if (null == input){
            throw new IllegalArgumentException("input should not be null.");
        }
        Deque<Character>stack = new ArrayDeque<>();

        for (int i=0; i<input.length(); i++){
            Character character = input.charAt(0);
            if(CLOSER_TO_OPENER.containsKey(character)){
                stack.push(character);
            } else if (CLOSER_TO_OPENER.containsValue(character)){
                if(stack.isEmpty()){
                    return false;
                }else{
                    Character head = stack.pop();
                    if(CLOSER_TO_OPENER.get(head).equals(character)){
                        return false;
                    }
                }
            }
        }
        return stack.isEmpty();
    }

    // ------------------------------------------------------------------------
    //  Demo entry point — quick smoke check while you iterate.
    //  The REAL verification lives in src/test/.../BalancedBracketsTest.java.
    //  See the README for how to run both.
    // ------------------------------------------------------------------------
    public static void main(String[] args) {
        String[] samples = {
                "",
                "()",
                "()[]{}",
                "([{}])",
                "(]",
                "([)]",
                "(",
                ")",
                "a(b + c) * (d - e)",
        };
        for (String s : samples) {
            System.out.printf("isBalanced(%-20s) = %s%n", "\"" + s + "\"", isBalanced(s));
        }
    }
}
