package com.enonic.xp.security;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public final class UserStoreKey
{
    private final static UserStoreKey SYSTEM = new UserStoreKey( "system" );

    private final static UserStoreKey DEFAULT = new UserStoreKey( "default" );

    private final static String RESERVED_USER_STORE_ID = PrincipalKey.ROLES_NODE_NAME;

    private final String id;

    public UserStoreKey( final String id )
    {
        Preconditions.checkArgument( !StringUtils.isBlank( id ), "UserStoreKey cannot be blank: %s", id );
        Preconditions.checkArgument( !RESERVED_USER_STORE_ID.equalsIgnoreCase( id ),
                                     "UserStoreKey id is reserved and cannot be used: %s", id );
        this.id = id;
    }

    public static UserStoreKey from(final String id) {
        return new UserStoreKey( id );
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

    public static UserStoreKey createDefault()
    {
        return DEFAULT;
    }
}
