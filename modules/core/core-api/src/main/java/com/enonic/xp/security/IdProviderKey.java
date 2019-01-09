package com.enonic.xp.security;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.util.CharacterChecker;

@Beta
public final class IdProviderKey
{
    private final static IdProviderKey SYSTEM = IdProviderKey.from( "system" );

    private final static IdProviderKey DEFAULT = IdProviderKey.from( "default" );

    private final static String RESERVED_USER_STORE_ID = PrincipalKey.ROLES_NODE_NAME;

    private final String id;

    public IdProviderKey( final String id )
    {
        Preconditions.checkArgument( !StringUtils.isBlank( id ), "IdProviderKey cannot be blank: %s", id );
        Preconditions.checkArgument( !RESERVED_USER_STORE_ID.equalsIgnoreCase( id ), "IdProviderKey id is reserved and cannot be used: %s",
                                     id );
        this.id = CharacterChecker.check( id, "Invalid IdProviderKey [" + id + "]" );
    }

    public static IdProviderKey from( final String id )
    {
        return new IdProviderKey( id );
    }

    @Override
    public String toString()
    {
        return this.id;
    }

    public static IdProviderKey system()
    {
        return SYSTEM;
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    public static IdProviderKey createDefault()
    {
        return DEFAULT;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof IdProviderKey ) )
        {
            return false;
        }
        final IdProviderKey idProviderKey = (IdProviderKey) o;
        return id.equals( idProviderKey.id );
    }
}
