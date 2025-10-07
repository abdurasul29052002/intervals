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
     *         otherwise {@code false}
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
}
