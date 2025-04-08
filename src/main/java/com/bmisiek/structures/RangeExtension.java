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
}
