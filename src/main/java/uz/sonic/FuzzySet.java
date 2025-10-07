package uz.sonic;

public record FuzzySet(double value, double left, double right, Interval interval) {

    public FuzzySet(double value, double left, double right) {
        this(value, left, right, new Interval(value - left, value + right));
    }

    public FuzzySet(double value, Interval interval) {
        this(value, value - interval.start(), interval.end() - value, interval);
    }

    public FuzzySet add(FuzzySet other) {
        return new FuzzySet(
                this.value + other.value,
                this.interval.add(other.interval)
        );
    }

    public FuzzySet subtract(FuzzySet other) {
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
                this.value / other.value,
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

