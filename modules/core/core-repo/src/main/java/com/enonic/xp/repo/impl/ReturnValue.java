package com.enonic.xp.repo.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

public class ReturnValue
{
    private final List<Object> values = Lists.newArrayList();

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
}
