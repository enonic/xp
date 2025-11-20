package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.StorageSource;

public class StoreRequest
{
    private final StorageData data;

    private final StorageSource storage;

    private final boolean forceRefresh;

    private final int timeout;

    private final String id;

    private final NodePath path;

    private final String parent;

    private final String routing;

    private StoreRequest( Builder builder )
    {
        this.data = builder.data;
        this.storage = builder.storage;
        this.forceRefresh = builder.forceRefresh;
        this.timeout = builder.timeout;
        this.id = builder.id;
        this.path = builder.path;
        this.routing = builder.routing;
        this.parent = builder.parent;

    }

    public static Builder create()
    {
        return new Builder();
    }

    public StorageData getData()
    {
        return this.data;
    }

    public StorageSource getStorage()
    {
        return storage;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public boolean isForceRefresh()
    {
        return forceRefresh;
    }

    public String getId()
    {
        return id;
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
        private StorageData data;

        private StorageSource storage;

        private boolean forceRefresh;

        private int timeout = 30;

        private String id;

        private NodePath path;

        private String parent;

        private String routing;

        private Builder()
        {
        }

        public Builder data( StorageData data )
        {
            this.data = data;
            return this;
        }

        public Builder storage( StorageSource settings )
        {
            this.storage = settings;
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

        public Builder parent( final String parent )
        {
            this.parent = parent;
            return this;
        }

        public Builder routing( final String routing )
        {
            this.routing = routing;
            return this;
        }

        public StoreRequest build()
        {
            return new StoreRequest( this );
        }
    }


    @Override
    public String toString()
    {
        return "StoreRequest{" + "data=" + data + ", storage=" + storage + ", forceRefresh=" + forceRefresh + ", timeout=" + timeout +
            ", id='" + id + '\'' + ", path=" + path + ", parent='" + parent + '\'' + ", routing='" + routing + '\'' + '}';
    }
}
