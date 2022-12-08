import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * This application simply does a binomial coefficient calculation and responds with the answer when fed two numbers
 * called the deck and the hand. This could have easily been done but the twist for this program is, you cannot use
 * anything more than 8 bytes to represent a number. Thus, BigInteger is out of the question and when dealing with
 * factorials, numbers can get very large very quickly. That's why this program represents numbers an integer arrays
 * and does base level calculation on every digit to figure out the whole.
 */
public class ProbabilityPoker {
    /**
     * A size for the int arrays being created to hold the large numbers
     */
    private static final int MAX = 10000;
    /**
     * A default for the size of the deck.
     */
    private long deck = 52;
    /**
     * A default for the size of the hand.
     */
    private long hand = 5;

    /**
     * Main entry point for the program. Instantiate a Probability Poker object and use the run method. One
     * instance has been conveniently provided.
     *
     * @param args no arguments.
     */
    public static void main(String[] args) {
        ProbabilityPoker newGame = new ProbabilityPoker();
        newGame.run();
    }

    /**
     * Used as the trunk for all processes to branch from. Asks the user the size of the deck and hand they want to
     * calculate and estimates the binomial coefficient of those numbers.
     */
    private void run() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to probability poker!");
        do {
            System.out.println("What is the size of your deck? ");
            while (scan.hasNextLine()) {
                try {
                    String deckInput = scan.nextLine();
                    setDeck(Long.parseLong(deckInput));
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please input a positive integer. ");
                }
            }
            System.out.println();
            System.out.println("What is the size of your hand? ");
            while (scan.hasNextLine()) {
                try {
                    String handInput = scan.nextLine();
                    setHand(Long.parseLong(handInput));
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please input a positive integer. ");
                }
            }
            if (hand > deck) {
                System.out.println("Your hand cannot be greater than your deck!");
            } else if (hand == deck) {
                System.out.println("\nUnique Hands: 1\n");
            } else {
                try {
                    long start = System.nanoTime();
                    System.out.println("\nUnique Hands: " + binomialCoefficientCalculator(getDeck(), getHand()) + "\n");
                    long end = System.nanoTime();
                    long duration = (end - start) / 1000000;
                    System.out.println("That took " + duration + " milliseconds.");
                } catch (NumberFormatException e) {
                    System.out.println("Output is larger than 64 bit");
                    }
            }
        } while (true);
    }


    /**
     * The method which is used as the base to calculate !n, k(n-k)!, for the final result n!/k!(n-k)!.
     *
     * @param n the set of numbers to be checked.
     * @param k the combination of numbers from the set, n.
     * @return the binomial coefficient of n & k.
     */
    private long binomialCoefficientCalculator(long n, long k) {
        int[] divisor = largeFactorial(k);
        int[] dividend = largeFactorial(n - k + 1, n);
        return longDivision(divisor, dividend);
    }

    /**
     * Takes two integers represented in int[] arrays and does long division on them.
     * @param divisor
     * @param dividend
     * @return the product of the vision represented in 64 bits.
     */
    private long longDivision(int[] divisor, int[] dividend) {
        int[] result = new int[dividend.length];
        List<Integer> accumulator = new ArrayList<>();
        for (int i = 0; i < dividend.length; i++) {
            accumulator.add(dividend[i]);
            if (divisor.length - 1 > i) {
                continue;
            }
            // converting List to int[] for 'isLesserThan' check
            int[] accPrim = new int[accumulator.size()];
            for (int k = 0; k < accPrim.length; k++) {
                accPrim[k] = accumulator.get(k);
            }
            if (!(isLesserThan(divisor, accPrim) == 1 || isLesserThan(divisor, accPrim) == 2)) {
                continue;
            }
            result[i] = (int) divide(accPrim, divisor);
            int[] b = multiply(divisor, new int[]{result[i]});
            int[] q = subtract(accPrim, b);
            accumulator.clear();
            for (int j = 0; j < q.length; j++) {
                accumulator.add(q[j]);
            }
        }

        return flattenArray(result);
    }

    /**
     * Converts an integer represented in an array format and flattens it to a 64bit long. E.g., [1],[2],[3] -> 123.
     * @param a the array to be flattened.
     * @return the flattened array.
     */
    private long flattenArray(int[] a) {
        StringBuilder stringArray = new StringBuilder();
        for (int i : a) {
            stringArray.append(i);
        }
        return Long.parseLong(stringArray.toString());
    }

    /**
     * The divide method takes two int arrays and divides them together using the multiple subtraction method.
     * Both int arrays should all be holding positive numbers and the result of this method will print out the result
     * of the division without the remainder.
     *
     * @param dividend if this was represented as a fraction, the dividend would be the top number.
     * @param divisor  if this was represented a fraction, the dividend would be the bottom number.
     * @return an int representing how many times the divisor goes into the dividend disregarding any remainder.
     */
    private long divide(int[] dividend, int[] divisor) {
        int iteration = 0;
        int zeroes = 0;
        for (int i = 0; i < dividend.length; i++) {
            if (dividend[i] == 0) {
                zeroes++;
            }
        }
        if (dividend.length == zeroes) {
            return 0;
        }
        zeroes = 0;
        for (int i = 0; i < divisor.length; i++) {
            if (divisor[i] == 0) {
                zeroes++;
            }
        }
        if (divisor.length == zeroes) {
            return 0;
        }
        int[] result = subtract(dividend, divisor);
        iteration++;
        if (isLesserThan(result, divisor) == 0 || isLesserThan(result, divisor) == 2) {
            do {
                result = subtract(result, divisor);
                iteration++;

            } while (isLesserThan(divisor, result) == 1 | isLesserThan(divisor, result) == 2);
        }
        return iteration;
    }

    /**
     * Checks two int array to see if one integer represented as an array is lesser than the other.
     *
     * @param isThis         the int[] to be checked
     * @param lesserThanThis the int[] to be checked by.
     * @return 0 = 'isThis' is lesser than 'lessThanThis',
     *         1 = 'isThis' is greater than 'lesserThanThis',
     *         2 = equal.
     */
    private int isLesserThan(int[] isThis, int[] lesserThanThis) {
        isThis = removePadding(isThis);
        lesserThanThis = removePadding(lesserThanThis);
        int numZeroes;
        int[] bottomPadded = new int[isThis.length];
        if (isThis.length < lesserThanThis.length) {
            return 1;
        } else if (isThis.length != lesserThanThis.length) {
            numZeroes = isThis.length - lesserThanThis.length;
            System.arraycopy(lesserThanThis, 0, bottomPadded, numZeroes, lesserThanThis.length);
            lesserThanThis = bottomPadded;
        }
        for (int i = 0; i < isThis.length; i++) {
            if (isThis[i] < lesserThanThis[i]) {
                return 1;
            } else if (isThis[i] > lesserThanThis[i]) {
                return 0;
            }
        }
        return 2;
    }

    /**
     * This method can convert factorials and store them in an int[] array at whatever length you specify
     * (default is 100). How it works: an array is created of size 100 and the first element is put as one. A loop is
     * then established which will start at one and go all the way up to your chosen factorial number. with each step
     * the algorithm will multiply j with the product of the previous iteration, and it will keep going until the
     * factorial number is reached.
     * <p>
     * The multiplication is done by using the fundamental multiplication algorithm and numbers are stored into an
     * int array. By using an int array, this circumvents any overflow that can happen with insufficient data types.
     *
     * @param factorialNumber the number which is used for the factorial. I.e., the 'n' in '!n'.
     * @return an int array which can hold a large number.
     */
    private int[] largeFactorial(long factorialNumber) {
        int[] result = new int[MAX];
        result[0] = 1;
        int resultSize = 1;
        int j = 2;

        while (j <= factorialNumber) {
            resultSize = multiplyWithIncrement(j, result, resultSize);
            j++;
        }
        // takes the results array and flips it around to its correct order. Also removes right padding.
        int[] nonReversedNoPadding = new int[resultSize];

        for (int k = resultSize - 1; k >= 0; k--) {
            nonReversedNoPadding[resultSize - 1 - k] = result[k];
        }
        return nonReversedNoPadding;
    }

    private int[] largeFactorial(long start, long factorialNumber) {
        int[] result = new int[MAX];
        result[0] = 1;
        int resultSize = 1;
        int j = (int) start;

        while (j <= factorialNumber) {
            resultSize = multiplyWithIncrement(j, result, resultSize);
            j++;
        }
        // takes the results array and flips it around to its correct order. Also removes right padding.
        int[] nonReversedNoPadding = new int[resultSize];

        for (int k = resultSize - 1; k >= 0; k--) {
            nonReversedNoPadding[resultSize - 1 - k] = result[k];
        }
        return nonReversedNoPadding;
    }

    /**
     * Takes two int arrays which represent large numbers and simulates american style subtraction (the way you
     * were taught in school with two large numbers on top of each other). This method assumes every digit in the
     * int arrays are positive and the result given will be stripped of any padding.
     *
     * @param minuend    an int array, the value for which another is to be subtracted.
     * @param subtrahend an int array, the quantity or number to be subtracted from another.
     * @return the result of the minuend subtracted by the subtrahend. the resulting array will be trimmed of
     * empty elements as well.
     */
    private int[] subtract(int[] minuend, int[] subtrahend) {
        int[] top;
        int[] bottom;

        top = minuend.clone();
        bottom = subtrahend.clone();
        int[] bottomPadded = new int[top.length];
        int[] difference = new int[top.length];
        int numZeroes;
        if (top.length != bottom.length) {
            numZeroes = top.length - bottom.length;
            System.arraycopy(bottom, 0, bottomPadded, numZeroes, bottom.length);
        } else {
            bottomPadded = bottom.clone();
        }
        for (int n = top.length - 1; n >= 0; n--) {
            if (top[n] - bottomPadded[n] < 0) {
                int slide = 1;
                while (top[n - slide] == 0) {
                    slide++;
                }
                while ((n - slide) != n) {
                    top[n - slide]--;
                    slide--;
                    top[n - slide] += 10;
                }
            }
            difference[n] = top[n] - bottomPadded[n];
        }
        difference = removePadding(difference);
        return difference;
    }

    /**
     * This is a method which is used in the largeFactorial method. Essential what happens is this method runs
     * within a loop. The step is initiated to 1 inside largeFactorial and with every increment, it multiplies the
     * increment number with the product of the previous calculation held inside the results array.
     * <p>
     * result: v     v     v      v    v
     * E.g.,   1*2 = 2*3 = 6*4 = 24*5=120
     * step:     ^     ^     ^      ^
     * <p>
     * The numbers in the array are dealt with reversed. For example, the number 123 is represented as {3,2,1}. This is
     * done for easier handling when using carry digits.
     *
     * @param step   the current iteration of the loop.
     * @param result the result array being formulated with each step.
     * @param size   the size of the result (not the maximum size of element slots in the array).
     * @return the size of the results array reversed.
     */
    private int multiplyWithIncrement(int step, int[] result, int size) {
        int carry = 0;
        for (int i = 0; i < size; i++) {
            int product = result[i] * step + carry;
            result[i] = product % 10;
            carry = product / 10;
        }
        while (carry != 0) {
            result[size] = carry % 10;
            carry = carry / 10;
            size++;
        }
        return size;
    }

    /**
     * This method takes two int arrays representing large numbers and multiplies them together simulating the long
     * multiplication algorithm.
     *
     * @param first  a large number represented as an int array.
     * @param second a large number represented as an int array.
     * @return the product of two large numbers multiplied together.
     */
    private int[] multiply(int[] first, int[] second) {
        int[] result = new int[MAX];
        int resultSize = 1;
        int carry = 0;
        int zeroes = 0;
        int tempSize = 1;
        // selecting number from second row
        for (int i = second.length - 1; i >= 0; i--) {
            int[] temp = new int[MAX];
            if (zeroes > 0) {
                for (int q = 0; q < zeroes; q++) {
                    temp[q] = 0;
                    tempSize++;
                }
            }
            for (int j = first.length - 1; j >= 0; j--) {
                int product = (second[i] * first[j] + temp[tempSize - 1]);
                temp[tempSize - 1] = product % 10;
                carry = product / 10;
                temp[tempSize] = carry;
                carry = 0;
                tempSize++;
            }
            for (int k = 0; k < tempSize; k++) {
                int product = result[k] + temp[k] + carry;
                result[k] = product % 10;
                carry = product / 10;

            }
            resultSize = tempSize - 1;
            zeroes++;
            tempSize = 1;
        }
        int[] nonReversedNoPadding = new int[resultSize + 1];
        for (int k = resultSize; k >= 0; k--) {
            nonReversedNoPadding[resultSize - k] = result[k];
        }
        nonReversedNoPadding = removePadding(nonReversedNoPadding);
        return nonReversedNoPadding;
    }

    /**
     * Removes left padded zeroes from an int array
     *
     * @param input an int array
     * @return the int array with removed left padded zeroes.
     */
    private int[] removePadding(int[] input) {
        //remove leading zeroes from the result
        int counter = 0;
        if (input[0] == 0) {
            for (int j : input) {
                if (j == 0) {
                    counter++;
                } else {
                    break;
                }
            }
        }
        if (counter > 0 && counter != input.length) {
            return Arrays.copyOfRange(input, counter, input.length);
        } else {
            return input;
        }
    }

    public long getDeck() {
        return deck;
    }

    public void setDeck(long deck) {
        this.deck = deck;
    }

    public long getHand() {
        return hand;
    }

    public void setHand(long hand) {
        this.hand = hand;
    }
}

