package uz.sonic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Interval {
    private double start;
    private double end;

    public Interval(double start, double end) {
        if (start > end) {
            throw new IllegalArgumentException("Start must be less than or equal to end.");
        }
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "[" + start + ", " + end + "]";
    }

    public Interval add(Interval other){
        return new Interval(this.start + other.start, this.end + other.end);
    }

    public Interval subtract(Interval other){
        return new Interval(this.start - other.end, this.end - other.start);
    }

    public Interval multiply(Interval other) {
        double a = this.start * other.start;
        double b = this.start * other.end;
        double c = this.end * other.start;
        double d = this.end * other.end;

        double newStart = Math.min(Math.min(a, b), Math.min(c, d));
        double newEnd = Math.max(Math.max(a, b), Math.max(c, d));

        return new Interval(newStart, newEnd);
    }

    public Interval divide(Interval other) {
        if (other.start <= 0 && other.end >= 0) {
            throw new ArithmeticException("Division by an interval that spans zero is undefined.");
        }

        double r1 = 1.0 / other.start;
        double r2 = 1.0 / other.end;
        var reciprocal = new Interval(Math.min(r1, r2), Math.max(r1, r2));
        return this.multiply(reciprocal);
    }

    public boolean contains(double value) {
        return value >= start && value <= end;
    }

    public boolean intersects(Interval other) {
        return this.end >= other.start && other.end >= this.start;
    }
}
