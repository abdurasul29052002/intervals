# intervals

A tiny, precise interval mathematics library for Java. It provides:

- Interval: immutable closed intervals [start, end] with BigDecimal precision
- FuzzySet: simple triangular fuzzy sets built on top of intervals

Both types are implemented as Java records and are immutable. Arithmetic is performed with MathContext.DECIMAL64 for a good balance of speed and precision.


## Features

- Precise interval arithmetic with BigDecimal
  - add, subtract, multiply, divide
  - contains(value), intersects(other)
  - safe division: throws ArithmeticException if divisor interval spans zero
- Triangular fuzzy sets
  - construct via (value, left, right) or (value, interval)
  - add, subtract, multiply, divide (element-wise on value and interval)
  - contains(value) and intersects(interval) via the underlying interval
- Java 21, small API surface, immutable by design
- Apache-2.0 licensed


## Installation

Maven:

```xml
<dependency>
  <groupId>uz.sonic</groupId>
  <artifactId>intervals</artifactId>
  <version>0.0.2</version>
</dependency>
```

Gradle (Kotlin DSL):

```kotlin
dependencies {
  implementation("uz.sonic:intervals:0.0.2")
}
```

Gradle (Groovy DSL):

```groovy
dependencies {
  implementation 'uz.sonic:intervals:0.0.2'
}
```


## Quick start

### Interval

```java
import uz.sonic.interval.Interval;
import java.math.BigDecimal;

Interval a = new Interval(new BigDecimal("1.5"), new BigDecimal("3.0")); // [1.5, 3.0]
Interval b = new Interval(new BigDecimal("2"),   new BigDecimal("4.5"));   // [2, 4.5]

Interval sum   = a.add(b);        // [1.5+2, 3.0+4.5] = [3.5, 7.5]
Interval diff  = a.subtract(b);   // [1.5-4.5, 3.0-2] = [-3.0, 1.0]
Interval prod  = a.multiply(b);   // min/max of all boundary products
Interval quot  = a.divide(b);     // multiply by reciprocal if b does not span zero

boolean has2   = a.contains(new BigDecimal("2"));     // true
boolean touch  = a.intersects(b);                      // true (they overlap)

System.out.println(sum);  // prints: [3.5, 7.5]
```

Division safety:

```java
Interval c = new Interval(new BigDecimal("-1"), new BigDecimal("1")); // spans zero
// The following will throw ArithmeticException
// Interval bad = a.divide(c);
```

### FuzzySet

```java
import uz.sonic.sets.FuzzySet;
import uz.sonic.interval.Interval;
import java.math.BigDecimal;

// Triangular fuzzy set with center=10, left dev=2, right dev=3
FuzzySet fx = new FuzzySet(new BigDecimal("10"), new BigDecimal("2"), new BigDecimal("3"));
// Underlying interval is [10-2, 10+3] = [8, 13]

// Or build from a center and an explicit interval
FuzzySet fy = new FuzzySet(new BigDecimal("5"), new Interval(new BigDecimal("4"), new BigDecimal("7")));

FuzzySet fsum  = fx.add(fy);
FuzzySet fdiff = fx.subtract(fy);
FuzzySet fprod = fx.multiply(fy);
FuzzySet fquot = fx.divide(fy); // may throw if fy's interval spans zero

boolean inside = fx.contains(new BigDecimal("9"));            // true, 9 ∈ [8, 13]
boolean cross  = fx.intersects(new Interval(new BigDecimal("0"), new BigDecimal("9"))); // true

System.out.println(fx); // prints: {10, 2, 3}
```


## API overview

- uz.sonic.interval.Interval
  - record Interval(BigDecimal start, BigDecimal end)
  - add(Interval), subtract(Interval), multiply(Interval), divide(Interval)
  - contains(BigDecimal), intersects(Interval)
  - toString(): "[start, end]"
- uz.sonic.sets.FuzzySet
  - record FuzzySet(BigDecimal value, BigDecimal left, BigDecimal right, Interval interval)
  - constructors: FuzzySet(value, left, right), FuzzySet(value, interval)
  - add(FuzzySet), subtract(FuzzySet), multiply(FuzzySet), divide(FuzzySet)
  - contains(BigDecimal), intersects(Interval)
  - toString(): "{value, left, right}"


## Javadoc

- Local build: mvn javadoc:javadoc (output under target/apidocs)
- Project site: https://intervals.sonic.uz


## Requirements

- Java 21+
- Maven/Gradle for dependency management


## License

Apache License 2.0. See LICENSE for details.


## Links

- Source code: https://github.com/abdurasul29052002/intervals
- Maven coordinates: uz.sonic:intervals:0.0.2
- Issue tracker: use GitHub issues