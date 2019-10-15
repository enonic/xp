package com.enonic.xp.repo.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
}
