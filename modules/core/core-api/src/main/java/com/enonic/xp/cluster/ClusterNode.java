package com.enonic.xp.cluster;

public class ClusterNode
{
    private final ClusterNodeId id;

    private ClusterNode( final ClusterNodeId id )
    {
        this.id = id;
    }

    public static ClusterNode from( final String id )
    {
        return new ClusterNode( ClusterNodeId.from( id ) );
    }

    public ClusterNodeId getId()
    {
        return id;
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

    @Override
    public String toString()
    {
        return id.toString();
    }
}
