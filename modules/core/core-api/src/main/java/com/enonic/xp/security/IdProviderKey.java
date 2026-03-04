package com.enonic.xp.security;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.core.internal.NameValidator;


@NullMarked
public final class IdProviderKey
    implements Serializable
{
    @Serial
    private static final long serialVersionUID = 0;

    private static final NameValidator ID_VALIDATOR = NameValidator.NAME.extend( IdProviderKey.class )
        .invalidChars(
            NameValidator.NAME_ILLEGAL_CHARACTERS + NameValidator.HTML_SPECIAL_CHARACTERS + SecurityConstants.PRINCIPAL_KEY_SEPARATOR +
                " " )
        .build();

    private static final IdProviderKey SYSTEM = new IdProviderKey( "system" );

    private final String id;

    private IdProviderKey( final String id )
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
        return switch ( Objects.requireNonNull( id, "IdProviderKey cannot be null" ) )
        {
            case "system" -> SYSTEM;
            default ->
            {
                if ( SecurityConstants.ROLES_NODE_NAME.equalsIgnoreCase( id ) )
                {
                    throw new IllegalArgumentException(
                        "IdProviderKey is reserved and cannot be used: " + SecurityConstants.ROLES_NODE_NAME );
                }
                yield new IdProviderKey( ID_VALIDATOR.validate( id ) );
            }
        };
    }

    public static IdProviderKey system()
    {
        return SYSTEM;
    }
}
