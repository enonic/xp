package com.enonic.xp.repo.impl.storage;

import org.apache.commons.lang.NotImplementedException;

import com.enonic.xp.repo.impl.StorageName;
import com.enonic.xp.repository.RepositoryId;

public abstract class BaseStorageName
    implements StorageName
{
    public final static String DIVIDER = "-";

    private final String name;

    protected BaseStorageName( final String name )
    {
        this.name = name;
    }

    protected static String getStorageName( final String prefix, final RepositoryId repositoryId )
    {
        return prefix + DIVIDER + repositoryId.toString();
    }

    @Override
    public String getName()
    {
        return name;
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

        final BaseStorageName that = (BaseStorageName) o;

        return !( name != null ? !name.equals( that.name ) : that.name != null );

    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        throw new NotImplementedException( "Must be implemented in inheritors" );
    }
}


