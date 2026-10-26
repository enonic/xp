package com.enonic.xp.repo.impl.elasticsearch.result;

import java.util.Set;
import java.util.stream.IntStream;

import com.enonic.xp.sortvalues.SortValuesProperty;

public class SortValuesPropertyFactory
{

    public static SortValuesProperty create( final Object[] values, final Set<Integer> hiddenPositions )
    {
        final Object[] visibleValues = values == null || hiddenPositions.isEmpty()
            ? values
            : IntStream.range( 0, values.length ).filter( i -> !hiddenPositions.contains( i ) ).mapToObj( i -> values[i] ).toArray();

        return SortValuesProperty.create().
            values( visibleValues ).
            build();
    }

}
