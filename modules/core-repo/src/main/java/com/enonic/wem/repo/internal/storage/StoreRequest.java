package com.enonic.wem.repo.internal.storage;

import com.google.common.collect.Multimap;

import com.enonic.xp.node.NodePath;

public class StoreRequest
{
    private final StorageData data;

    private final StorageSettings settings;

    private final boolean forceRefresh;

    private final int timeout;

    private final String id;

    private final NodePath path;

    private StoreRequest( Builder builder )
    {
        this.data = builder.data;
        this.settings = builder.settings;
        this.forceRefresh = builder.forceRefresh;
        this.timeout = builder.timeout;
        this.id = builder.id;
        this.path = builder.path;

    }

    public static StoreRequest.Builder from( final StoreRequest source )
    {
        return create().
            settings( source.getSettings() ).
            nodePath( source.getPath() ).
            id( source.getId() ).
            data( source.getData() ).
            forceRefresh( source.forceRefresh );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Multimap<String, Object> getEntries()
    {
        return this.data.getValues();
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

    public String getId()
    {
        return id;
    }

    public NodePath getPath()
    {
        return path;
    }

    public static final class Builder
    {
        private StorageData data;

        private StorageSettings settings;

        private boolean forceRefresh = false;

        private int timeout = 5;

        private String id;

        private NodePath path;


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

        public Builder id( String id )
        {
            this.id = id;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.path = nodePath;
            return this;
        }


        public StoreRequest build()
        {
            return new StoreRequest( this );
        }
    }
}
