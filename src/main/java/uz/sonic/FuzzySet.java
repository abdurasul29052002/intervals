package uz.sonic;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FuzzySet {
    private double value;
    private double left;
    private double right;

    public FuzzySet(double value, double left, double right){
        this.value = value;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "{" + left + ", " + value + ", " + right + "}";
    }

    public Interval toInterval(){
        return new Interval(value - left, value + right);
    }
}
