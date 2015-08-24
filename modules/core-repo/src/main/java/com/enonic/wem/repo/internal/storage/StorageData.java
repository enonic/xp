package com.enonic.wem.repo.internal.storage;

import java.time.Instant;
import java.util.Set;

import com.google.common.collect.Sets;

public class StorageData
{
    private final String parent;

    private final String routing;

    private final String id;

    private final Set<StorageDataEntry> dataEntries;

    private StorageData( Builder builder )
    {
        parent = builder.parent;
        routing = builder.routing;
        id = builder.id;
        dataEntries = builder.dataEntries;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public Set<StorageDataEntry> getDataEntries()
    {
        return dataEntries;
    }


    public String getParent()
    {
        return parent;
    }

    public String getRouting()
    {
        return routing;
    }

    public String getId()
    {
        return id;
    }

    public static final class Builder
    {
        private String parent;

        private String routing;

        private String id;

        private Set<StorageDataEntry> dataEntries = Sets.newHashSet();

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

        public Builder id( String id )
        {
            this.id = id;
            return this;
        }


        public Builder addStringValue( final String key, final String value )
        {
            this.dataEntries.add( new StringStorageDataEntry( key, value ) );
            return this;
        }

        public Builder addInstant( final String key, final Instant instant )
        {
            this.dataEntries.add( new InstantStorageDataEntry( key, instant ) );
            return this;
        }

        public Builder dataEntries( Set<StorageDataEntry> dataEntries )
        {
            this.dataEntries = dataEntries;
            return this;
        }

        public StorageData build()
        {
            return new StorageData( this );
        }
    }
}
