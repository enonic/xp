package com.enonic.xp.security;

import java.io.Serializable;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.util.CharacterChecker;

import static com.google.common.base.Strings.nullToEmpty;

@PublicApi
public final class IdProviderKey
    implements Serializable
{
    private static final long serialVersionUID = 0;

    private static final IdProviderKey SYSTEM = IdProviderKey.from( "system" );

    private static final IdProviderKey DEFAULT = IdProviderKey.from( "default" );

    private static final String RESERVED_ID_PROVIDER_KEY = PrincipalKey.ROLES_NODE_NAME;

    private final String id;

    public IdProviderKey( final String id )
    {
        Preconditions.checkArgument( !nullToEmpty( id ).isBlank(), "IdProviderKey cannot be blank: %s", id );
        Preconditions.checkArgument( !RESERVED_ID_PROVIDER_KEY.equalsIgnoreCase( id ),
                                     "IdProviderKey id is reserved and cannot be used: %s", id );
        this.id = CharacterChecker.check( id, "Invalid IdProviderKey [" + id + "]" );
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof IdProviderKey && id.equals( ( (IdProviderKey) o ).id );
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public String toString()
    {
        return this.id;
    }

    public static IdProviderKey from( final String id )
    {
        return new IdProviderKey( id );
    }

    public static IdProviderKey system()
    {
        return SYSTEM;
    }

    public static IdProviderKey createDefault()
    {
        return DEFAULT;
    }
}
