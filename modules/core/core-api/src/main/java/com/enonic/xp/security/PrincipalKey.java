package com.enonic.xp.security;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.xp.core.internal.NameValidator;
import com.enonic.xp.node.NodePath;


public final class PrincipalKey
    implements Serializable
{
    @Serial
    private static final long serialVersionUID = 0;

    private static final NameValidator ID_VALIDATOR = NameValidator.NAME.extend( PrincipalKey.class )
        .invalidChars(
            NameValidator.NAME_ILLEGAL_CHARACTERS + NameValidator.HTML_SPECIAL_CHARACTERS + SecurityConstants.PRINCIPAL_KEY_SEPARATOR +
                " " )
        .build();

    private static final Pattern REF_PATTERN = Pattern.compile( "(role):([^:]+)|(user|group):([^:]+):([^:]+)" );

    private static final PrincipalKey ANONYMOUS_PRINCIPAL = new PrincipalKey( IdProviderKey.system(), PrincipalType.USER, "anonymous" );

    private static final PrincipalKey SUPER_USER_PRINCIPAL = new PrincipalKey( IdProviderKey.system(), PrincipalType.USER, "su" );

    static final PrincipalKey EVERYONE_ROLE = new PrincipalKey( null, PrincipalType.ROLE, "system.everyone" );

    static final PrincipalKey AUTHENTICATED_ROLE = new PrincipalKey( null, PrincipalType.ROLE, "system.authenticated" );

    static final PrincipalKey ADMIN_ROLE = new PrincipalKey( null, PrincipalType.ROLE, "system.admin" );

    public static final String IDENTITY_NODE_NAME = "identity";

    public static final String ROLES_NODE_NAME = SecurityConstants.ROLES_NODE_NAME;

    public static final String GROUPS_NODE_NAME = "groups";

    public static final String USERS_NODE_NAME = "users";

    private final IdProviderKey idProviderKey;

    private final PrincipalType type;

    private final String id;

    private PrincipalKey( final IdProviderKey idProviderKey, final PrincipalType type, final String id )
    {
        this.idProviderKey = idProviderKey;
        this.type = Objects.requireNonNull( type );
        this.id = Objects.requireNonNull( id );
    }

    public static PrincipalKey from( final String principalKey )
    {
        switch ( Objects.requireNonNull( principalKey, "PrincipalKey cannot be null" ) )
        {
            case "user:system:anonymous":
                return ANONYMOUS_PRINCIPAL;
            case "user:system:su":
                return SUPER_USER_PRINCIPAL;
            case "role:system.everyone":
                return EVERYONE_ROLE;
            case "role:system.authenticated":
                return AUTHENTICATED_ROLE;
            case "role:system.admin":
                return ADMIN_ROLE;
            default: // no predefined key found. Let's parse
        }

        final Matcher matcher = REF_PATTERN.matcher( principalKey );
        if ( !matcher.matches() )
        {
            throw new IllegalArgumentException( "Not a valid principal key [" + principalKey + "]" );
        }

        final String typeStr;
        final IdProviderKey idProviderKey;
        final String id;
        if ( matcher.group( 1 ) != null )
        {
            typeStr = matcher.group( 1 );
            idProviderKey = null;
            id = matcher.group( 2 );
        }
        else
        {
            typeStr = matcher.group( 3 );
            final String idProvider = matcher.group( 4 );
            idProviderKey = IdProviderKey.from( idProvider );
            id = matcher.group( 5 );
        }
        final PrincipalType type = PrincipalType.valueOf( typeStr.toUpperCase() );

        return from( idProviderKey, type, id );
    }

    public PrincipalType getType()
    {
        return type;
    }

    public String getId()
    {
        return id;
    }

    public boolean isUser()
    {
        return this.type == PrincipalType.USER;
    }

    public boolean isGroup()
    {
        return this.type == PrincipalType.GROUP;
    }

    public boolean isRole()
    {
        return this.type == PrincipalType.ROLE;
    }

    public boolean isAnonymous()
    {
        return this.equals( ANONYMOUS_PRINCIPAL );
    }

    private static PrincipalKey from( final IdProviderKey idProviderKey, final PrincipalType type, final String id )
    {
        return switch ( type )
        {
            case USER -> PrincipalKey.ofUser( idProviderKey, id );
            case GROUP -> PrincipalKey.ofGroup( idProviderKey, id );
            case ROLE -> PrincipalKey.ofRole( id );
        };
    }

    @Override
    public String toString()
    {
        return type == PrincipalType.ROLE
            ? String.join( SecurityConstants.PRINCIPAL_KEY_SEPARATOR, type.toString().toLowerCase(), id )
            : String.join( SecurityConstants.PRINCIPAL_KEY_SEPARATOR, type.toString().toLowerCase(), idProviderKey.toString(), id );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof final PrincipalKey that ) )
        {
            return false;
        }

        return Objects.equals( type, that.type ) && Objects.equals( idProviderKey, that.idProviderKey ) && Objects.equals( id, that.id );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( type, idProviderKey, id );
    }

    public static PrincipalKey ofUser( final IdProviderKey idProvider, final String userId )
    {
        return new PrincipalKey( Objects.requireNonNull( idProvider, "User idProvider cannot be null" ), PrincipalType.USER,
                                 ID_VALIDATOR.withSubject( "User id" ).validate( userId ) );
    }

    public static PrincipalKey ofGroup( final IdProviderKey idProvider, final String groupId )
    {
        return new PrincipalKey( Objects.requireNonNull( idProvider, "Group idProvider cannot be null" ), PrincipalType.GROUP,
                                 ID_VALIDATOR.withSubject( "Group id" ).validate( groupId ) );
    }

    public static PrincipalKey ofRole( final String roleId )
    {
        return new PrincipalKey( null, PrincipalType.ROLE, ID_VALIDATOR.withSubject( "Role id" ).validate( roleId ) );
    }

    public static PrincipalKey ofAnonymous()
    {
        return ANONYMOUS_PRINCIPAL;
    }

    public static PrincipalKey ofSuperUser()
    {
        return SUPER_USER_PRINCIPAL;
    }

    public IdProviderKey getIdProviderKey()
    {
        return idProviderKey;
    }

    public NodePath toPath()
    {
        if ( this.isRole() )
        {
            return NodePath.create( NodePath.ROOT )
                .addElement( IDENTITY_NODE_NAME )
                .addElement( ROLES_NODE_NAME )
                .addElement( getId() )
                .build();
        }
        else
        {
            final String folderName = this.isGroup() ? GROUPS_NODE_NAME : USERS_NODE_NAME;
            return NodePath.create( NodePath.ROOT )
                .addElement( IDENTITY_NODE_NAME )
                .addElement( idProviderKey.toString() )
                .addElement( folderName )
                .addElement( getId() )
                .build();
        }
    }
}
