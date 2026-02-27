package com.enonic.xp.security;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.core.internal.NameValidator;

@PublicApi
public final class IdProviderKey
    implements Serializable
{
    @Serial
    private static final long serialVersionUID = 0;

    private static final NameValidator ID_PROVIDER_KEY_VALIDATOR = NameValidator.builder( "IdProviderKey", NameValidator.NAME )
        .maxLength( SecurityConstants.ID_PROVIDER_KEY_MAX_LENGTH )
        .invalidChars( NameValidator.HTML_FORBITTEN_CHARS + NameValidator.FILENAME_FORBITTEN_CHARS )
        .build();

    private static final IdProviderKey SYSTEM = new IdProviderKey( "system" );

    private static final IdProviderKey DEFAULT = new IdProviderKey( "default" );

    private static final String RESERVED_ID_PROVIDER_KEY = PrincipalKey.ROLES_NODE_NAME;

    private final String id;

    public IdProviderKey( final String id )
    {
        this.id = Objects.requireNonNull( id );
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
        return switch ( id )
        {
            case "system" -> SYSTEM;
            case "default" -> DEFAULT;
            case null, default ->
            {
                if ( RESERVED_ID_PROVIDER_KEY.equalsIgnoreCase( id ) )
                {
                    throw new IllegalArgumentException( "IdProviderKey is reserved and cannot be used: " + RESERVED_ID_PROVIDER_KEY );
                }
                yield new IdProviderKey( ID_PROVIDER_KEY_VALIDATOR.validate( id ) );
            }
        };
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
