package com.enonic.wem.repo.internal.index.result;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class GetResultNew
{
    private final String id;

    private Multimap<String, Object> values;


    private GetResultNew( final Builder builder )
    {
        this.id = builder.id;
        this.values = builder.values;
    }

    private GetResultNew()
    {
        id = null;
    }

    public static GetResultNew empty()
    {
        return new GetResultNew();
    }

    public boolean isEmpty()
    {
        return this.id == null;
    }

    public Collection<Object> get( final String key )
    {
        return this.values.get( key );
    }

    public Multimap<String, Object> getValues()
    {
        return values;
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


    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String id;

        final Multimap<String, Object> values = ArrayListMultimap.create();

        private Builder()
        {
        }

        public Builder id( String id )
        {
            this.id = id;
            return this;
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

        public GetResultNew build()
        {
            return new GetResultNew( this );
        }
    }
}
