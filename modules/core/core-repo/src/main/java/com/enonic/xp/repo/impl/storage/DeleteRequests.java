package com.enonic.xp.repo.impl.storage;

import java.util.List;

import com.enonic.xp.repo.impl.StorageSource;

public class DeleteRequests
{
    private final List<String> ids;

    private final StorageSource settings;

    private final boolean forceRefresh;

    private final int timeout;

    private DeleteRequests( final Builder builder )
    {
        ids = builder.ids;
        settings = builder.settings;
        forceRefresh = builder.forceRefresh;
        timeout = builder.timeout;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public List<String> getIds()
    {
        return ids;
    }

    public StorageSource getSettings()
    {
        return settings;
    }

    public boolean isForceRefresh()
    {
        return forceRefresh;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public String getTimeoutAsString()
    {
        return timeout + "s";
    }

    public static final class Builder
    {
        private StorageSource settings;

        private boolean forceRefresh;

        private int timeout = 5;

        private List<String> ids;

        private Builder()
        {
        }

        public Builder settings( final StorageSource val )
        {
            settings = val;
            return this;
        }

        public Builder forceRefresh( final boolean val )
        {
            forceRefresh = val;
            return this;
        }

        public Builder timeout( final int val )
        {
            timeout = val;
            return this;
        }

        public DeleteRequests build()
        {
            return new DeleteRequests( this );
        }

        public Builder ids( final List<String> val )
        {
            ids = val;
            return this;
        }
    }
}
