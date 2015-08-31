package com.enonic.wem.repo.internal.index.result;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.Lists;

public class ReturnValue
{
    private Collection<Object> values = Lists.newArrayList();

    public Object getSingleValue()
    {
        return values.iterator().next();
    }

    public Collection<Object> getValues()
    {
        return values;
    }

    public static ReturnValue create( final Object values )
    {
        final ReturnValue returnValue = new ReturnValue();
        returnValue.add( values );

        return returnValue;
    }

    public void add( final Object value )
    {
        if ( value instanceof Collection )
        {
            values.addAll( (Collection) value );
        }
        else if ( value instanceof Object[] )
        {
            values.addAll( Arrays.asList( (Object[]) value ) );
        }
        else
        {
            values.add( value );
        }
    }
}
