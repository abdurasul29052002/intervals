package uz.sonic;

import java.math.BigDecimal;
import java.math.MathContext;

public record Interval(BigDecimal start, BigDecimal end) {

    private static final MathContext MC = MathContext.DECIMAL64;

    public Interval {
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("Start must be less than or equal to end.");
        }
    }

    @Override
    public String toString() {
        return "[" + start + ", " + end + "]";
    }

    public Interval add(Interval other) {
        return new Interval(
                this.start.add(other.start, MC),
                this.end.add(other.end, MC)
        );
    }

    public Interval subtract(Interval other) {
        return new Interval(
                this.start.subtract(other.end, MC),
                this.end.subtract(other.start, MC)
        );
    }

    public Interval multiply(Interval other) {
        BigDecimal a = this.start.multiply(other.start, MC);
        BigDecimal b = this.start.multiply(other.end, MC);
        BigDecimal c = this.end.multiply(other.start, MC);
        BigDecimal d = this.end.multiply(other.end, MC);

        BigDecimal newStart = a.min(b).min(c).min(d);
        BigDecimal newEnd = a.max(b).max(c).max(d);

        return new Interval(newStart, newEnd);
    }

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

    public boolean contains(BigDecimal value) {
        return value.compareTo(start) >= 0 && value.compareTo(end) <= 0;
    }

    public boolean intersects(Interval other) {
        return this.end.compareTo(other.start) >= 0 && other.end.compareTo(this.start) >= 0;
    }
}
