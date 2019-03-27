package com.enonic.xp.repo.impl.index;

import java.util.Objects;

public class StemmedIndexValueType
    implements IndexValueTypeInterface
{

    public static final String STEMMED_INDEX_PREFIX = "_stemmed_";

    private String value;

    public StemmedIndexValueType( final String value )
    {
        this.value = value.startsWith( STEMMED_INDEX_PREFIX ) ? value : STEMMED_INDEX_PREFIX + value;
    }

    @Override
    public String getPostfix()
    {
        return value;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final StemmedIndexValueType that = (StemmedIndexValueType) o;
        return Objects.equals( value, that.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( value );
    }
}
