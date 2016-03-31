package com.enonic.xp.security;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.util.CharacterChecker;

@Beta
public final class PathGuardKey
{
    private final static PathGuardKey ADMIN_LOGIN = PathGuardKey.from( "admin" );

    private final static PathGuardKey ADMIN_REST = PathGuardKey.from( "admin-rest" );

    private final String id;

    public PathGuardKey( final String id )
    {
        Preconditions.checkArgument( !StringUtils.isBlank( id ), "PathGuardKey cannot be blank: %s", id );
        this.id = CharacterChecker.check( id, "Invalid PathGuardKey [" + id + "]" );
    }

    public static PathGuardKey from( final String id )
    {
        return new PathGuardKey( id );
    }

    @Override
    public String toString()
    {
        return this.id;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PathGuardKey ) )
        {
            return false;
        }
        final PathGuardKey userStoreKey = (PathGuardKey) o;
        return id.equals( userStoreKey.id );
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    public static PathGuardKey admin()
    {
        return ADMIN_LOGIN;
    }

    public static PathGuardKey adminRest()
    {
        return ADMIN_REST;
    }
}
