package uz.sonic;

public class Main {
    public static void main(String[] args) {
        var set1 = new FuzzySet(2, 0.2, 0.3);
        var set2 = new FuzzySet(5, 0.4, 0.7);
        System.out.println(set1.add(set2));
    }
}
