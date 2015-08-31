package com.enonic.wem.repo.internal.index.result;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class ResultFieldValues
{
    private Multimap<String, Object> values;

    private ResultFieldValues( final Builder builder )
    {
        this.values = builder.values;
    }

    public Object getSingleValue( final String key )
    {
        final Collection<Object> values = this.values.get( key );

        if ( values == null || values.isEmpty() )
        {
            return null;
        }

        return values.iterator().next();
    }

    public Collection<Object> get( final String key )
    {
        return this.values.get( key );
    }

    public Multimap<String, Object> getValues()
    {
        return values;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        final Multimap<String, Object> values = ArrayListMultimap.create();

        private Builder()
        {
        }

        public Builder add( final String key, final Object value )
        {
            if ( value instanceof Collection )
            {
                values.putAll( key, ( (Collection) value ) );
            }
            else if ( value instanceof Object[] )
            {
                values.putAll( key, Arrays.asList( (Object[]) value ) );
            }
            else
            {
                values.put( key, value );
            }

            return this;
        }

        public ResultFieldValues build()
        {
            return new ResultFieldValues( this );
        }
    }

}
