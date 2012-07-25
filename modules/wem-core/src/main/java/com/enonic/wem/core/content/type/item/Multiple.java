package com.enonic.wem.core.content.type.item;


import com.google.common.base.Preconditions;

public class Multiple
{

    private int minimumEntries;

    /**
     * Zero means unlimited entries;
     */
    private int maximumEntries;

    public Multiple( final int minimumEntries, final int maximumEntries )
    {
        Preconditions.checkArgument( minimumEntries >= 0 );
        Preconditions.checkArgument( maximumEntries >= 0 );

        this.minimumEntries = minimumEntries;
        this.maximumEntries = maximumEntries;
    }

    public int getMinimumEntries()
    {
        return minimumEntries;
    }

    public int getMaximumEntries()
    {
        return maximumEntries;
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

        final Multiple multiple = (Multiple) o;

        if ( maximumEntries != multiple.maximumEntries )
        {
            return false;
        }
        if ( minimumEntries != multiple.minimumEntries )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = minimumEntries;
        result = 31 * result + maximumEntries;
        return result;
    }
}
