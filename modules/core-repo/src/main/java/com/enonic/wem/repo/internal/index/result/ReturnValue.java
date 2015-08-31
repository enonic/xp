package com.enonic.wem.repo.internal.index.result;

import java.util.List;

import com.google.common.collect.Lists;

public class ReturnValue
{
    private final List<Object> values;

    public Object getValue()
    {
        return values.get( 0 );
    }

    public List<Object> getValues()
    {
        return values;
    }

    private ReturnValue( final List<Object> values )
    {
        this.values = values;
    }

    public static ReturnValue value( final Object value )
    {
        return new ReturnValue( Lists.newArrayList( value ) );
    }

    public static ReturnValue values( final List<Object> values )
    {
        return new ReturnValue( values );

    }


}
