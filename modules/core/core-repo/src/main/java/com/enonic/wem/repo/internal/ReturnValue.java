package com.enonic.wem.repo.internal;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.Lists;

public class ReturnValue
{
    private final Collection<Object> values = Lists.newArrayList();

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
}
