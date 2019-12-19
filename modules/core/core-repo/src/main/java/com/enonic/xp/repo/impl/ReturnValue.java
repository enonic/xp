package com.enonic.xp.repo.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ReturnValue
{
    private final List<Object> values = new ArrayList<>();

    public static ReturnValue create( final Object values )
    {
        final ReturnValue returnValue = new ReturnValue();
        returnValue.add( values );

        return returnValue;
    }

    public Object getSingleValue()
    {
        return values.iterator().next();
    }

    public List<Object> getValues()
    {
        return values;
    }

    public void add( final Object values )
    {
        if ( values instanceof Collection )
        {
            this.values.addAll( (Collection) values );
        }
        else if ( values instanceof Object[] )
        {
            this.values.addAll( Arrays.asList( (Object[]) values ) );
        }
        else
        {
            this.values.add( values );
        }
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
        final ReturnValue that = (ReturnValue) o;
        return Objects.equals( values, that.values );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( values );
    }
}
