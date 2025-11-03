package uz.sonic;

import uz.sonic.interval.Interval;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        var interval = new Interval(
                BigDecimal.valueOf(0),
                BigDecimal.valueOf(Math.PI)
        );
        System.out.println(interval.cosTaylor(10));
        System.out.println(interval.cos());
    }
}
