package com.enonic.wem.security;

public final class UserStoreKey
{
    private final static UserStoreKey SYSTEM = new UserStoreKey( "system" );

    private final String id;

    public UserStoreKey( final String id )
    {
        this.id = id;
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
        if ( !( o instanceof UserStoreKey ) )
        {
            return false;
        }
        final UserStoreKey userStoreKey = (UserStoreKey) o;
        return id.equals( userStoreKey.id );
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    public static UserStoreKey system()
    {
        return SYSTEM;
    }
}
