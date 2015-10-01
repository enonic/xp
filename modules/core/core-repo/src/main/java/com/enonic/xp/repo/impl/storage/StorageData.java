package com.enonic.xp.repo.impl.storage;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class StorageData
{
    private final Multimap<String, Object> values;

    private StorageData( Builder builder )
    {
        this.values = builder.values;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Multimap<String, Object> getValues()
    {
        return values;
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

        public StorageData build()
        {
            return new StorageData( this );
        }
    }
}
