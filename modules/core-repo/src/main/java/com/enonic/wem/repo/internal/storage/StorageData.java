package com.enonic.wem.repo.internal.storage;

import java.time.Instant;
import java.util.Set;

import com.google.common.collect.Sets;

public class StorageData
{
    private final Set<StorageDataEntry> dataEntries;

    private StorageData( Builder builder )
    {
        dataEntries = builder.dataEntries;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Set<StorageDataEntry> getDataEntries()
    {
        return dataEntries;
    }

    public static final class Builder
    {
        private Set<StorageDataEntry> dataEntries = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder dataEntries( Set<StorageDataEntry> dataEntries )
        {
            this.dataEntries = dataEntries;
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

        public StorageData build()
        {
            return new StorageData( this );
        }
    }
}
