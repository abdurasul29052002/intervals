package uz.sonic.interval;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Represents a closed interval {@code [start, end]} on the real number line, where both boundaries
 * are included. This class supports basic arithmetic operations such as addition, subtraction,
 * multiplication, and division, performed using {@link BigDecimal} arithmetic to ensure
 * high precision.
 *
 * <p>All operations return new immutable {@code Interval} instances. The original
 * objects remain unchanged.</p>
 *
 * <p>Note: Division by an interval that spans zero (i.e., includes 0 within its range)
 * is undefined and will throw an {@link ArithmeticException}.</p>
 */
public record Interval(BigDecimal start, BigDecimal end) {

    private static final MathContext MC = MathContext.DECIMAL64;

    /**
     * Constructs an {@code Interval} with the specified start and end values.
     *
     * @param start the lower bound of the interval (inclusive)
     * @param end   the upper bound of the interval (inclusive)
     * @throws IllegalArgumentException if {@code start} is greater than {@code end}
     */
    public Interval {
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("Start must be less than or equal to end.");
        }
    }

    /**
     * Returns a string representation of this interval in the form [start, end].
     *
     * @return a string representation of the interval
     */
    @Override
    public String toString() {
        return "[" + start + ", " + end + "]";
    }

    /**
     * Adds the corresponding bounds of this interval and another interval.
     *
     * @param other the interval to add
     * @return a new {@code Interval} representing the element-wise sum
     */
    public Interval add(Interval other) {
        return new Interval(
                this.start.add(other.start, MC),
                this.end.add(other.end, MC)
        );
    }

    /**
     * Subtracts another interval from this one, by subtracting the other’s upper bound
     * from this interval’s lower bound, and the other’s lower bound from this interval’s upper bound.
     *
     * @param other the interval to subtract
     * @return a new {@code Interval} representing the result of the subtraction
     */
    public Interval subtract(Interval other) {
        return new Interval(
                this.start.subtract(other.end, MC),
                this.end.subtract(other.start, MC)
        );
    }

    /**
     * Multiplies this interval by another interval. All combinations of boundary multiplications
     * are computed, and the result is the smallest interval that contains all possible products.
     *
     * @param other the interval to multiply by
     * @return a new {@code Interval} representing the product
     */
    public Interval multiply(Interval other) {
        BigDecimal a = this.start.multiply(other.start, MC);
        BigDecimal b = this.start.multiply(other.end, MC);
        BigDecimal c = this.end.multiply(other.start, MC);
        BigDecimal d = this.end.multiply(other.end, MC);

        BigDecimal newStart = a.min(b).min(c).min(d);
        BigDecimal newEnd = a.max(b).max(c).max(d);

        return new Interval(newStart, newEnd);
    }

    /**
     * Divides this interval by another interval. The operation is performed by multiplying
     * this interval by the reciprocal of the divisor interval.
     *
     * <p>If the divisor interval spans zero (i.e., contains 0), the operation is undefined
     * and will throw an {@link ArithmeticException}.</p>
     *
     * @param other the interval to divide by
     * @return a new {@code Interval} representing the quotient
     * @throws ArithmeticException if the divisor interval spans zero
     */
    public Interval divide(Interval other) {
        // Division by an interval containing zero is not allowed
        if (other.start.compareTo(BigDecimal.ZERO) <= 0 && other.end.compareTo(BigDecimal.ZERO) >= 0) {
            throw new ArithmeticException("Division by an interval that spans zero is undefined.");
        }

        BigDecimal r1 = BigDecimal.ONE.divide(other.start, MC);
        BigDecimal r2 = BigDecimal.ONE.divide(other.end, MC);
        Interval reciprocal = new Interval(r1.min(r2), r1.max(r2));

        return this.multiply(reciprocal);
    }

    /**
     * Checks if the specified value is contained within this interval.
     *
     * @param value the value to test
     * @return {@code true} if the value lies between {@code start} and {@code end} (inclusive),
     * otherwise {@code false}
     */
    public boolean contains(BigDecimal value) {
        return value.compareTo(start) >= 0 && value.compareTo(end) <= 0;
    }

    /**
     * Determines whether this interval intersects with another interval.
     *
     * @param other the interval to test for intersection
     * @return {@code true} if the two intervals overlap or touch at the boundary, otherwise {@code false}
     */
    public boolean intersects(Interval other) {
        return this.end.compareTo(other.start) >= 0 && other.end.compareTo(this.start) >= 0;
    }

    /**
     * Computes the factorial of a non-negative integer using an iterative approach.
     * The factorial of a number n is defined as the product of all positive integers less than or equal to n.
     *
     * @param n the non-negative integer for which the factorial is to be computed
     * @return the factorial of the given integer as a {@code BigDecimal}
     * @throws IllegalArgumentException if {@code n} is negative
     */
    private static BigDecimal factorial(int n) {
        var result = BigDecimal.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigDecimal.valueOf(i));
        }
        return result;
    }

    /**
     * Computes the sine of the interval using an approximation based on the Taylor series.
     * The sine is computed up to the specified number of terms, providing an interval
     * that bounds the result of the sine operation for the given interval range.
     *
     * @param terms the number of terms to use in the Taylor series approximation. A higher value
     *              results in a more accurate approximation but increases computation time.
     * @return a new {@code Interval} representing the sine of the interval.
     */
    public Interval sin(int terms) {
        var result = new Interval(BigDecimal.ZERO, BigDecimal.ZERO);
        var power = this;
        boolean negative = false;

        for (int k = 0; k < terms; k++) {
            int exp = 2 * k + 1;
            var fact = factorial(exp);
            var term = power.divide(fact);

            result = negative ? result.subtract(term) : result.add(term);
            negative = !negative;

            if (k < terms - 1) {
                power = power.multiply(this.multiply(this));
            }
        }
        return result;
    }

    /**
     * Computes the cosine of the interval using an approximation based on the Taylor series.
     * The cosine is computed up to the specified number of terms, providing an interval
     * that bounds the result of the cosine operation for the given interval range.
     *
     * @param terms the number of terms to use in the Taylor series approximation. A higher value
     *              results in a more accurate approximation but increases computation time.
     * @return a new {@code Interval} representing the cosine of the interval.
     */
    public Interval cos(int terms) {
        var result = new Interval(BigDecimal.ZERO, BigDecimal.ZERO);
        var power = new Interval(BigDecimal.ONE, BigDecimal.ONE);
        boolean negative = false;

        for (int k = 0; k < terms; k++) {
            int exp = 2 * k;
            var fact = factorial(exp);
            var term = power.divide(fact);

            result = negative ? result.subtract(term) : result.add(term);
            negative = !negative;

            if (k < terms - 1) {
                power = power.multiply(this.multiply(this));
            }
        }
        return result;
    }

    /**
     * Divides the interval by a scalar value. If the scalar is negative, the interval's boundaries
     * are reversed to maintain the correct order.
     *
     * @param scalar the non-zero scalar value by which the interval is to be divided
     * @return a new {@code Interval} representing the result of the division
     * @throws ArithmeticException if the scalar is zero
     */
    public Interval divide(BigDecimal scalar) {
        if (BigDecimal.ZERO.equals(scalar)) {
            throw new ArithmeticException("Division by zero");
        }
        var startDiv = this.start.divide(scalar, MC);
        var endDiv = this.end.divide(scalar, MC);

        if (scalar.compareTo(BigDecimal.ZERO) < 0) {
            return new Interval(endDiv, startDiv);
        } else {
            return new Interval(startDiv, endDiv);
        }
    }

}
