package com.enonic.xp.repo.impl;

import java.util.HashMap;
import java.util.Map;

public class ReturnValues
{
    private final Map<String, ReturnValue> returnValues;

    private ReturnValues( final Builder builder )
    {
        this.returnValues = builder.returnValues;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static ReturnValues empty()
    {
        return ReturnValues.create().build();
    }

    public Object getSingleValue( final String key )
    {
        final ReturnValue returnValue = returnValues.get( key );

        if ( returnValue == null )
        {
            return null;
        }

        return returnValue.getSingleValue();
    }

    public ReturnValue get( final String key )
    {
        return this.returnValues.get( key );
    }

    public Map<String, ReturnValue> getReturnValues()
    {
        return returnValues;
    }

    public static final class Builder
    {
        final Map<String, ReturnValue> returnValues = new HashMap<>();

        private Builder()
        {
        }

        public Builder add( final String key, final Object value )
        {
            final ReturnValue entry = returnValues.get( key );

            if ( entry == null )
            {
                this.returnValues.put( key, ReturnValue.create( value ) );
            }
            else
            {
                entry.add( value );
            }

            return this;
        }

        public ReturnValues build()
        {
            return new ReturnValues( this );
        }
    }

}
