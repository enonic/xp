package com.enonic.xp.security;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.CharacterChecker;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public final class PrincipalKey
    implements Serializable
{
    private static final long serialVersionUID = 0;

    private static final String SEPARATOR = ":";

    private static final Pattern REF_PATTERN = Pattern.compile( "^(role):([^:]+)|(user|group):([^:]+):([^:]+)$" );

    private static final PrincipalKey ANONYMOUS_PRINCIPAL = new PrincipalKey( IdProviderKey.system(), PrincipalType.USER, "anonymous" );

    private static final PrincipalKey SUPER_USER_PRINCIPAL = new PrincipalKey( IdProviderKey.system(), PrincipalType.USER, "su" );

    public static final String IDENTITY_NODE_NAME = "identity";

    public static final String ROLES_NODE_NAME = "roles";

    public static final String GROUPS_NODE_NAME = "groups";

    public static final String USERS_NODE_NAME = "users";

    private final IdProviderKey idProviderKey;

    private final PrincipalType type;

    private final String principalId;

    private PrincipalKey( final IdProviderKey idProviderKey, final PrincipalType type, final String principalId )
    {
        checkArgument( ( type == PrincipalType.ROLE ) || ( idProviderKey != null ), "Principal id provider cannot be null" );
        this.idProviderKey = idProviderKey;
        this.type = checkNotNull( type, "Principal type cannot be null" );
        checkArgument( !isNullOrEmpty( principalId ), "Principal id cannot be null or empty" );
        this.principalId = CharacterChecker.check( principalId, "Not a valid principal key [" + principalId + "]" );
    }

    public static PrincipalKey from( final String principalKey )
    {
        checkArgument( !isNullOrEmpty( principalKey ), "Principal key cannot be null or empty" );
        switch ( principalKey )
        {
            case "user:system:anonymous":
                return ANONYMOUS_PRINCIPAL;
            case "user:system:su":
                return SUPER_USER_PRINCIPAL;
            default: // no predefined key found. Let's parse
        }

        final Matcher matcher = REF_PATTERN.matcher( principalKey );
        if ( !matcher.find() )
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
        return principalId;
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
        switch ( type )
        {
            case USER:
                return PrincipalKey.ofUser( idProviderKey, id );
            case GROUP:
                return PrincipalKey.ofGroup( idProviderKey, id );
            case ROLE:
                return PrincipalKey.ofRole( id );

            default:
                throw new IllegalArgumentException( "Not a valid principal type [" + type + "]" );
        }
    }

    @Override
    public String toString()
    {
        if ( type == PrincipalType.ROLE )
        {
            return String.join( SEPARATOR, type.toString().toLowerCase(), principalId );
        }
        else
        {
            return String.join( SEPARATOR, type.toString().toLowerCase(), idProviderKey.toString(), principalId );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof PrincipalKey ) )
        {
            return false;
        }

        final PrincipalKey that = (PrincipalKey) o;

        return Objects.equals( type, that.type ) && Objects.equals( idProviderKey, that.idProviderKey ) &&
            Objects.equals( principalId, that.principalId );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( type, idProviderKey, principalId );
    }

    public static PrincipalKey ofUser( final IdProviderKey idProvider, final String userId )
    {
        return new PrincipalKey( idProvider, PrincipalType.USER, userId );
    }

    public static PrincipalKey ofGroup( final IdProviderKey idProvider, final String groupId )
    {
        return new PrincipalKey( idProvider, PrincipalType.GROUP, groupId );
    }

    public static PrincipalKey ofRole( final String roleId )
    {
        return new PrincipalKey( null, PrincipalType.ROLE, roleId );
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
                .addElement( getIdProviderKey().toString() )
                .addElement( folderName )
                .addElement( getId() )
                .build();
        }
    }
}
