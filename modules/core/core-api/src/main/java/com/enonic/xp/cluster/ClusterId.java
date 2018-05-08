package com.enonic.xp.cluster;

public class ClusterId
{
    private final String value;

    private ClusterId( final String value )
    {
        this.value = value;
    }

    public static ClusterId from( final String name )
    {
        return new ClusterId( name );
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

        final ClusterId that = (ClusterId) o;

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
