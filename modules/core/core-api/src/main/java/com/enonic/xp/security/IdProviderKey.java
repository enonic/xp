package com.enonic.xp.security;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.util.CharacterChecker;

import static com.google.common.base.Strings.nullToEmpty;

@PublicApi
public final class IdProviderKey
{
    private final static IdProviderKey SYSTEM = IdProviderKey.from( "system" );

    private final static IdProviderKey DEFAULT = IdProviderKey.from( "default" );

    private final static String RESERVED_ID_PROVIDER_KEY = PrincipalKey.ROLES_NODE_NAME;

    private final String id;

    public IdProviderKey( final String id )
    {
        Preconditions.checkArgument( !nullToEmpty( id ).isBlank(), "IdProviderKey cannot be blank: %s", id );
        Preconditions.checkArgument( !RESERVED_ID_PROVIDER_KEY.equalsIgnoreCase( id ),
                                     "IdProviderKey id is reserved and cannot be used: %s", id );
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
