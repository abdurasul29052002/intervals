package uz.sonic.sets;

import uz.sonic.interval.Interval;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Represents a fuzzy set, which is a mathematical model often used in fuzzy logic
 * and approximate reasoning to define memberships of elements in a set with gradual transitions.
 * This implementation uses triangular membership functions defined by a central value,
 * a left deviation, and a right deviation.
 *
 * <p>Each fuzzy set is associated with an interval represented by the range of
 * possible values spanned by the membership function. Arithmetic operations on fuzzy sets
 * are supported by composing their corresponding intervals and central values.</p>
 *
 * <p>Instances of this class are immutable once constructed.</p>
 *
 * @param value   the central value of the fuzzy set
 * @param left    the left deviation from the central value
 * @param right   the right deviation from the central value
 * @param interval the interval corresponding to the range of the fuzzy set
 */
public record FuzzySet(BigDecimal value, BigDecimal left, BigDecimal right, Interval interval) {

    private static final MathContext MC = MathContext.DECIMAL64;

    /**
     * Constructs a new {@code FuzzySet} with the specified central value, left deviation, and right deviation.
     * The interval of the fuzzy set is automatically calculated based on the provided parameters.
     *
     * @param value the central value of the fuzzy set
     * @param left the left deviation from the central value, used to calculate the lower bound of the interval
     * @param right the right deviation from the central value, used to calculate the upper bound of the interval
     */
    public FuzzySet(BigDecimal value, BigDecimal left, BigDecimal right) {
        this(value, left, right, new Interval(value.subtract(left), value.add(right)));
    }

    /**
     * Constructs a new {@code FuzzySet} with the specified central value and interval.
     * The left deviation and right deviation are automatically calculated based on
     * the provided interval and central value.
     *
     * @param value    the central value of the fuzzy set
     * @param interval the interval defining the bounds of the fuzzy set; the start and end
     *                 of the interval are used to calculate the left and right deviations
     */
    public FuzzySet(BigDecimal value, Interval interval) {
        this(value, value.subtract(interval.start()), interval.end().subtract(value), interval);
    }

    /**
     * Adds this fuzzy set to another fuzzy set, creating a new fuzzy set that represents the result
     * of the element-wise addition of their values and intervals.
     *
     * @param other the fuzzy set to add to this fuzzy set
     * @return a new {@code FuzzySet} representing the result of the addition
     */
    public FuzzySet add(FuzzySet other) {
        return new FuzzySet(
                this.value.add(other.value, MC),
                this.interval.add(other.interval)
        );
    }

    /**
     * Subtracts another {@code FuzzySet} from this {@code FuzzySet},
     * resulting in a new {@code FuzzySet} where the central values
     * and intervals are subtracted element-wise.
     *
     * @param other the {@code FuzzySet} to subtract from this {@code FuzzySet}
     * @return a new {@code FuzzySet} representing the result of the subtraction
     */
    public FuzzySet subtract(FuzzySet other) {
        return new FuzzySet(
                this.value.subtract(other.value, MC),
                this.interval.subtract(other.interval)
        );
    }

    /**
     * Multiplies this {@code FuzzySet} with another {@code FuzzySet}, resulting in a new {@code FuzzySet}
     * whose central value and interval are the element-wise products of the respective elements of the two sets.
     *
     * @param other the {@code FuzzySet} to multiply with this {@code FuzzySet}
     * @return a new {@code FuzzySet} representing the result of the multiplication
     */
    public FuzzySet multiply(FuzzySet other) {
        return new FuzzySet(
                this.value.multiply(other.value, MC),
                this.interval.multiply(other.interval)
        );
    }

    /**
     * Divides this {@code FuzzySet} by another {@code FuzzySet}, resulting in a new
     * {@code FuzzySet} where the central values and intervals are divided element-wise.
     *
     * @param other the {@code FuzzySet} to divide this {@code FuzzySet} by
     * @return a new {@code FuzzySet} representing the result of the division
     * @throws ArithmeticException if division by an interval that spans zero occurs
     */
    public FuzzySet divide(FuzzySet other) {
        return new FuzzySet(
                this.value.divide(other.value, MC),
                this.interval.divide(other.interval)
        );
    }

    /**
     * Determines whether the specified value is contained within the interval of this fuzzy set.
     *
     * @param val the value to check for inclusion within the interval
     * @return {@code true} if the specified value is within the interval boundaries, {@code false} otherwise
     */
    public boolean contains(BigDecimal val) {
        return this.interval.contains(val);
    }

    /**
     * Determines whether the interval of this fuzzy set intersects with the specified interval.
     *
     * @param other the interval to check for intersection with the interval of this fuzzy set
     * @return {@code true} if the intervals intersect, {@code false} otherwise
     */
    public boolean intersects(Interval other) {
        return this.interval.intersects(other);
    }

    /**
     * Provides a string representation of the {@code FuzzySet}. The representation
     * includes the central value, left deviation, and right deviation of the set.
     *
     * @return a string in the format "{value, left, right}" representing the fuzzy set
     */
    @Override
    public String toString() {
        return "{" + value + ", " + left + ", " + right + "}";
    }
}

