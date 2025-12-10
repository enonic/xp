package com.enonic.xp.repo.impl.storage;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;

import com.enonic.xp.index.IndexPath;

public class StorageData
{
    private final Map<String, Collection<Object>> values;

    private StorageData( Builder builder )
    {
        this.values = builder.builder.build().asMap();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Map<String, Collection<Object>> asValuesMap()
    {
        return values;
    }

    public static final class Builder
    {
        private final ImmutableMultimap.Builder<String, Object> builder = ImmutableListMultimap.builder();

        private Builder()
        {
        }

        public Builder add( final IndexPath key, final Object value )
        {
            Objects.requireNonNull( key );
            Objects.requireNonNull( value );

            if ( value instanceof Iterable )
            {
                builder.putAll( key.getPath(), ( (Iterable<?>) value ) );
            }
            else
            {
                builder.put( key.getPath(), value );
            }

            return this;
        }

        public StorageData build()
        {
            return new StorageData( this );
        }
    }
}
