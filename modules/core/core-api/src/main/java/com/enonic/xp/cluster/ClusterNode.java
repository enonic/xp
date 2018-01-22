package com.enonic.xp.cluster;

public class ClusterNode
{
    private final ClusterNodeId id;

    private ClusterNode( final ClusterNodeId id )
    {
        this.id = id;
    }

    private ClusterNode( final Builder builder )
    {
        id = builder.id;
    }

    public static ClusterNode from( final String id )
    {
        return new ClusterNode( ClusterNodeId.from( id ) );
    }

    public ClusterNodeId getId()
    {
        return id;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ClusterNodeId id;

        private Builder()
        {
        }

        public Builder id( final ClusterNodeId val )
        {
            id = val;
            return this;
        }

        public ClusterNode build()
        {
            return new ClusterNode( this );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final ClusterNode that = (ClusterNode) o;

        return id != null ? id.equals( that.id ) : that.id == null;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }
}
