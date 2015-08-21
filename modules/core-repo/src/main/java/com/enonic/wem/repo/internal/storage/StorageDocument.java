package com.enonic.wem.repo.internal.storage;

import java.util.Set;

public class StorageDocument
{
    private final StorageData data;

    private final StorageSettings settings;

    private final String id;

    private StorageDocument( Builder builder )
    {
        data = builder.data;
        settings = builder.settings;
        id = builder.id;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Set<StorageDataEntry> getEntries()
    {
        return this.data.getDataEntries();
    }

    public StorageSettings getSettings()
    {
        return settings;
    }

    public String getId()
    {
        return id;
    }


    public static final class Builder
    {
        private StorageData data;

        private StorageSettings settings;

        private String id;

        private Builder()
        {
        }

        public Builder data( StorageData data )
        {
            this.data = data;
            return this;
        }

        public Builder settings( StorageSettings settings )
        {
            this.settings = settings;
            return this;
        }

        public Builder id( String id )
        {
            this.id = id;
            return this;
        }

        public StorageDocument build()
        {
            return new StorageDocument( this );
        }
    }
}
