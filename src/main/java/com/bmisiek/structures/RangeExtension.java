package com.bmisiek.structures;


import org.springframework.data.domain.Range;

public class RangeExtension {
    public static Integer getLength(Range<Integer> range) throws UnsupportedOperationException {
        var lowerBound = range.getLowerBound();
        var upperBound = range.getUpperBound();

        if (!lowerBound.isBounded() || !upperBound.isBounded()) {
            throw new UnsupportedOperationException("Cannot calculate length of an unbounded range.");
        }

        var lowerBoundValue = lowerBound.getValue().orElseThrow();
        var upperBoundValue = upperBound.getValue().orElseThrow();

        var length = upperBoundValue - lowerBoundValue;
        if (lowerBound.isInclusive() && upperBound.isInclusive()) {
            length += 1;
        } else if (!lowerBound.isInclusive() && !upperBound.isInclusive()) {
            length -= 1;
        }

        return length;
    }

    /**
     * Given a range and a value adjust this value from Range(0,0) to given range
     *
     * e.g. Range(-3, 2) and 5 -> 2
     * @param range
     * @param value
     * @return
     */
    public static Integer AdjustToBounds(Range<Integer> range, Integer value) {
        return value + range.getLowerBound().getValue().orElseThrow();
    }

    /**
     * Given a range and a value adjusted to this range returns value adjusted to Range(0,0)
     *
     * e.g. Range(-3, 2) and 2 -> 5
     * @param range
     * @param value
     * @return
     */
    public static Integer AdjustFromBounds(Range<Integer> range, Integer value) {
        return value - range.getLowerBound().getValue().orElseThrow();
    }
}
