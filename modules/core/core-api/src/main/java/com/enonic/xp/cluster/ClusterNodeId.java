package com.enonic.xp.cluster;

public class ClusterNodeId
{
    private final String value;

    public static ClusterNodeId from( final String name )
    {
        return new ClusterNodeId( name );
    }

    private ClusterNodeId( final String value )
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return value;
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

        final ClusterNodeId that = (ClusterNodeId) o;

        return value != null ? value.equals( that.value ) : that.value == null;
    }

    @Override
    public int hashCode()
    {
        return value != null ? value.hashCode() : 0;
    }
}
