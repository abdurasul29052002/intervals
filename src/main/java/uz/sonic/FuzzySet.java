package uz.sonic;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FuzzySet {
    private final double value;
    private final double left;
    private final double right;
    private final Interval interval;

    public FuzzySet(double value, double left, double right){
        this.value = value;
        this.left = left;
        this.right = right;
        this.interval = new Interval(value - left, value + right);
    }

    public FuzzySet(double value, Interval interval){
        this.value = value;
        this.left = value - interval.getStart();
        this.right = interval.getEnd() - value;
        this.interval = interval;
    }

    public FuzzySet add(FuzzySet other){
        return new FuzzySet(
                this.value + other.value,
                this.interval.add(other.interval)
        );
    }

    public FuzzySet subtract(FuzzySet other){
        return new FuzzySet(
                this.value - other.value,
                this.interval.subtract(other.interval)
        );
    }

    public FuzzySet multiply(FuzzySet other) {
        return new FuzzySet(
                this.value * other.value,
                this.interval.multiply(other.interval)
        );
    }

    public FuzzySet divide(FuzzySet other) {
        return new FuzzySet(
                this.value - other.value,
                this.interval.divide(other.interval)
        );
    }

    public boolean contains(double value) {
        return this.interval.contains(value);
    }

    public boolean intersects(Interval other) {
        return this.interval.intersects(other);
    }

    @Override
    public String toString() {
        return "{" + value + ", " + left + ", " + right + "}";
    }
}
