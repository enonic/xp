package com.enonic.wem.repo.internal.storage;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

public class StorageData
{
    private final String parent;

    private final String routing;

    private final Map<String, StorageDataEntry> dataEntries;

    private StorageData( Builder builder )
    {
        this.parent = builder.parent;
        this.routing = builder.routing;
        this.dataEntries = builder.dataEntries;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Collection<StorageDataEntry> getDataEntries()
    {
        return dataEntries.values();
    }

    public StorageDataEntry get( final String path )
    {
        return this.dataEntries.get( path );
    }

    public String getParent()
    {
        return parent;
    }

    public String getRouting()
    {
        return routing;
    }

    public static final class Builder
    {
        private String parent;

        private String routing;

        private Map<String, StorageDataEntry> dataEntries = Maps.newHashMap();

        private Builder()
        {
        }

        public Builder parent( String parent )
        {
            this.parent = parent;
            return this;
        }

        public Builder routing( String routing )
        {
            this.routing = routing;
            return this;
        }

        public Builder addStringValue( final String key, final String value )
        {
            this.dataEntries.put( key, new StringStorageDataEntry( key, value ) );
            return this;
        }

        public Builder addInstant( final String key, final Instant instant )
        {
            this.dataEntries.put( key, new InstantStorageDataEntry( key, instant ) );
            return this;
        }

        public StorageData build()
        {
            return new StorageData( this );
        }
    }
}
