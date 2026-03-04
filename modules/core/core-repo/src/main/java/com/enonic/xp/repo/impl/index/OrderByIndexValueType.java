package com.enonic.xp.repo.impl.index;

import java.util.Locale;
import java.util.Objects;

public class OrderByIndexValueType
    implements IndexValueTypeInterface
{
    private static final String ORDER_BY_INDEX_PREFIX = "_orderby_";

    private final String value;

    public OrderByIndexValueType( final String language )
    {
        this.value = ORDER_BY_INDEX_PREFIX + language.toLowerCase( Locale.ROOT );
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
        final OrderByIndexValueType that = (OrderByIndexValueType) o;
        return Objects.equals( value, that.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( value );
    }
}
