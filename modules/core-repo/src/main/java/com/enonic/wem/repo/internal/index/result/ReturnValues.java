package com.enonic.wem.repo.internal.index.result;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

public class ReturnValues
{
    private final Map<String, ReturnValue> returnValues;

    private ReturnValues( final Builder builder )
    {
        this.returnValues = builder.returnValues;
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

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        final Map<String, ReturnValue> returnValues = Maps.newHashMap();

        private Builder()
        {
        }

        public Builder add( final String key, final String value )
        {
            return doAdd( key, value );
        }

        public Builder add( final String key, final Instant value )
        {
            return doAdd( key, value );
        }

        public Builder add( final String key, final Collection<Object> value )
        {
            return doAdd( key, value );
        }

        private Builder doAdd( final String key, final Object value )
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
