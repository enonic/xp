package com.enonic.xp.cluster;

public class ClusterProviderId
{
    private final String value;

    private ClusterProviderId( final String value )
    {
        this.value = value;
    }

    public static ClusterProviderId from( final String name )
    {
        return new ClusterProviderId( name );
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

        final ClusterProviderId that = (ClusterProviderId) o;

        return value != null ? value.equals( that.value ) : that.value == null;
    }

    @Override
    public int hashCode()
    {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return value;
    }
}
