package com.enonic.wem.core.content.config.field;


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
}
