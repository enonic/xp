package com.enonic.wem.api.userstore.statistics;

public final class UserStoreStatistics
{
    private int numUsers;

    private int numGroups;

    private int numRoles;

    public int getNumUsers()
    {
        return numUsers;
    }

    public int getNumGroups()
    {
        return numGroups;
    }

    public int getNumRoles()
    {
        return numRoles;
    }

    public void setNumUsers( final int numUsers )
    {
        this.numUsers = numUsers;
    }

    public void setNumGroups( final int numGroups )
    {
        this.numGroups = numGroups;
    }

    public void setNumRoles( final int numRoles )
    {
        this.numRoles = numRoles;
    }
}
