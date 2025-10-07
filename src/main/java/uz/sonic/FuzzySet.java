package uz.sonic;

import java.math.BigDecimal;
import java.math.MathContext;

public record FuzzySet(BigDecimal value, BigDecimal left, BigDecimal right, Interval interval) {

    private static final MathContext MC = MathContext.DECIMAL64;

    public FuzzySet(BigDecimal value, BigDecimal left, BigDecimal right) {
        this(value, left, right, new Interval(value.subtract(left), value.add(right)));
    }

    public FuzzySet(BigDecimal value, Interval interval) {
        this(value, value.subtract(interval.start()), interval.end().subtract(value), interval);
    }

    public FuzzySet add(FuzzySet other) {
        return new FuzzySet(
                this.value.add(other.value, MC),
                this.interval.add(other.interval)
        );
    }

    public FuzzySet subtract(FuzzySet other) {
        return new FuzzySet(
                this.value.subtract(other.value, MC),
                this.interval.subtract(other.interval)
        );
    }

    public FuzzySet multiply(FuzzySet other) {
        return new FuzzySet(
                this.value.multiply(other.value, MC),
                this.interval.multiply(other.interval)
        );
    }

    public FuzzySet divide(FuzzySet other) {
        return new FuzzySet(
                this.value.divide(other.value, MC),
                this.interval.divide(other.interval)
        );
    }

    public boolean contains(BigDecimal val) {
        return this.interval.contains(val);
    }

    public boolean intersects(Interval other) {
        return this.interval.intersects(other);
    }

    @Override
    public String toString() {
        return "{" + value + ", " + left + ", " + right + "}";
    }
}

