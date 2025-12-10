package com.enonic.xp.repo.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.enonic.xp.index.IndexPath;

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

    public String getStringValue( final IndexPath key )
    {
        final ReturnValue returnValue = returnValues.get( key.getPath() );

        if ( returnValue == null )
        {
            throw new NoSuchElementException( key.getPath() );
        }

        return returnValue.getSingleValue().toString();
    }

    public Optional<Object> getOptional( final IndexPath key )
    {
        final ReturnValue returnValue = returnValues.get( key.getPath() );

        return returnValue == null ? Optional.empty() : Optional.of( returnValue.getSingleValue() );
    }

    public ReturnValue get( final IndexPath key )
    {
        return this.returnValues.get( key.getPath() );
    }

    public static final class Builder
    {
        final Map<String, ReturnValue> returnValues = new HashMap<>();

        private Builder()
        {
        }

        public Builder add( final String key, final Object value )
        {
            return add( key, value instanceof Collection ? (Collection<?>) value : Collections.singleton( value ) );
        }

        public Builder add( final String key, final Collection<?> value )
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
