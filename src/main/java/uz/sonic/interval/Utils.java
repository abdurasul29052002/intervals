package uz.sonic.interval;

import java.math.BigDecimal;

class Utils {
    /**
     * Computes the factorial of a non-negative integer using an iterative approach.
     * The factorial of a number n is defined as the product of all positive integers less than or equal to n.
     *
     * @param n the non-negative integer for which the factorial is to be computed
     * @return the factorial of the given integer as a {@code BigDecimal}
     * @throws IllegalArgumentException if {@code n} is negative
     */
    public static BigDecimal factorial(int n) {
        var result = BigDecimal.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigDecimal.valueOf(i));
        }
        return result;
    }

}
