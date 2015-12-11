package com.enonic.xp.repo.impl.index.document;

import java.time.Instant;

public abstract class IndexValue<T>
{
    private T value;

    public IndexValue( final T value )
    {
        this.value = value;
    }

    public static IndexValueInstant create( final Instant value )
    {
        return new IndexValueInstant( value );
    }

    public static IndexValueString create( final String value )
    {
        return new IndexValueString( value );
    }

    public static IndexValueDouble create( final Double value )
    {
        return new IndexValueDouble( value );
    }

    public T getValue()
    {
        return value;
    }
}
