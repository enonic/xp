package com.enonic.wem.repo.internal.storage;

import com.enonic.wem.repo.internal.StorageSettings;

public class DeleteRequest
{
    private final String id;

    private final StorageSettings settings;

    private final boolean forceRefresh;

    private final int timeout;

    private DeleteRequest( Builder builder )
    {
        id = builder.id;
        settings = builder.settings;
        forceRefresh = builder.forceRefresh;
        this.timeout = builder.timeout;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getId()
    {
        return id;
    }

    public StorageSettings getSettings()
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
        private String id;

        private StorageSettings settings;

        private boolean forceRefresh = false;

        private int timeout = 5;

        private Builder()
        {
        }

        public Builder id( String id )
        {
            this.id = id;
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

        public Builder timeout( int timeout )
        {
            this.timeout = timeout;
            return this;
        }

        public DeleteRequest build()
        {
            return new DeleteRequest( this );
        }
    }
}
