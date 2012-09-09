package com.enonic.wem.api.userstore;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public final class UserStoreName
{
    private final static UserStoreName SYSTEM = new UserStoreName( "system" );

    private final String name;

    private UserStoreName( final String name )
    {
        this.name = name;
    }

    public boolean isSystem()
    {
        return SYSTEM.name.equals( this.name );
    }

    public int hashCode()
    {
        return this.name.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof UserStoreName ) && ( (UserStoreName) o ).name.equals( this.name );
    }

    public String toString()
    {
        return this.name;
    }

    public static UserStoreName system()
    {
        return SYSTEM;
    }

    public static UserStoreName from( final String name )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( name ), "UserStore name cannot be null or empty" );
        return new UserStoreName( name );
    }
}
