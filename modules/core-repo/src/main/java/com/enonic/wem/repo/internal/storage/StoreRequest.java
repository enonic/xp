package com.enonic.wem.repo.internal.storage;

import java.util.Set;

public class StoreRequest
{
    private final StorageData data;

    private final StorageSettings settings;

    private final boolean forceRefresh;

    private final int timeout;

    private StoreRequest( Builder builder )
    {
        this.data = builder.data;
        this.settings = builder.settings;
        this.forceRefresh = builder.forceRefresh;
        this.timeout = builder.timeout;
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

    public StorageData getData()
    {
        return data;
    }

    public String getTimeout()
    {
        return timeout + "s";
    }

    public boolean isForceRefresh()
    {
        return forceRefresh;
    }

    public static final class Builder
    {
        private StorageData data;

        private StorageSettings settings;

        private boolean forceRefresh = false;

        private int timeout = 5;

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

        public Builder forceRefresh( boolean forceRefresh )
        {
            this.forceRefresh = forceRefresh;
            return this;
        }

        public Builder timeout( final int timeout )
        {
            this.timeout = timeout;
            return this;
        }

        public StoreRequest build()
        {
            return new StoreRequest( this );
        }
    }
}
