package com.enonic.xp.repo.impl.elasticsearch.result;

import com.enonic.xp.sortvalues.SortValuesProperty;

public class SortValuesPropertyFactory
{

    public static SortValuesProperty create( final Object[] values )
    {
        return SortValuesProperty.create().
            values( values ).
            build();
    }

}
