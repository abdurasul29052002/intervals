package uz.sonic.interval;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Function;

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
            var temp = start;
            start = end;
            end = temp;
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
     * Computes the sine of the interval using an approximation based on the Taylor series.
     * The sine is computed up to the specified number of terms, providing an interval
     * that bounds the result of the sine operation for the given interval range.
     *
     * @param terms the number of terms to use in the Taylor series approximation. A higher value
     *              results in a more accurate approximation but increases computation time.
     * @return a new {@code Interval} representing the sine of the interval.
     */
    public Interval sinTaylor(int terms) {
        Function<BigDecimal, BigDecimal> sinTaylorScalar = (x) -> {
            var sum = x;     // sin(x) ≈ x - x^3/3! + ...
            var a_i = x;     // a_0 = x
            var negative = true;

            for (long n = 1; n < terms; n++) {
                // k = -x^2 / ((2n+2)*(2n+3))
                var ratio = x.multiply(x)
                        .divide(BigDecimal.valueOf((2 * n) * (2 * n + 1)), 2, RoundingMode.HALF_UP);
                a_i = a_i.multiply(ratio);
                sum = negative ? sum.subtract(a_i) : sum.add(a_i);
                negative = !negative;
            }
            return sum;
        };
        return new Interval(
                sinTaylorScalar.apply(this.start),
                sinTaylorScalar.apply(this.end)
        );
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
    public Interval cosTaylor(int terms) {
        Function<BigDecimal, BigDecimal> cosTaylorScalar = (x) -> {
            var sum = BigDecimal.ONE;
            var a_i = BigDecimal.ONE;
            boolean negative = true;

            for (long i = 0; i < terms; i++) {
                var ratio = x.multiply(x).divide(
                        BigDecimal.valueOf((2 * i + 1) * (2 * i + 2)),
                        2,
                        RoundingMode.HALF_UP
                );
                a_i = a_i.multiply(ratio);
                sum = negative ? sum.subtract(a_i) : sum.add(a_i);
                negative = !negative;
            }
            return sum;
        };
        return new Interval(
                cosTaylorScalar.apply(this.start),
                cosTaylorScalar.apply(this.end)
        );
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

    public Interval expTaylor(int terms) {
        var sum = new Interval(BigDecimal.ZERO, BigDecimal.ZERO);
        var a_i = new Interval(BigDecimal.valueOf(1), BigDecimal.valueOf(1));
        sum = sum.add(a_i);
        for (int i = 1; i < terms; i++) {
            var ratio = this.divide(BigDecimal.valueOf(i));
            a_i = a_i.multiply(ratio);
            sum = sum.add(a_i);
        }
        return sum;
    }

    public Interval sin() {
        return new Interval(
                BigDecimal.valueOf(Math.sin(start.doubleValue())),
                BigDecimal.valueOf(Math.sin(end.doubleValue()))
        );
    }

    public Interval cos() {
        return new Interval(
                BigDecimal.valueOf(Math.cos(start.doubleValue())),
                BigDecimal.valueOf(Math.cos(end.doubleValue()))
        );
    }

    public Interval exp() {
        return new Interval(
                BigDecimal.valueOf(Math.exp(start.doubleValue())),
                BigDecimal.valueOf(Math.exp(end.doubleValue()))
        );
    }
}
