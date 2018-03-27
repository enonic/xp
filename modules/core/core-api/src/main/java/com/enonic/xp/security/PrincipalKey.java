package com.enonic.xp.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.CharacterChecker;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Beta
public final class PrincipalKey
{
    private final static String SEPARATOR = ":";

    private final static Pattern REF_PATTERN = Pattern.compile( "^(?:(role):([^:]+))|(user|group):([^:]+):([^:]+)$" );

    private static final PrincipalKey ANONYMOUS_PRINCIPAL = new PrincipalKey();

    private static final PrincipalKey SUPER_USER_PRINCIPAL = new PrincipalKey( UserStoreKey.system(), PrincipalType.USER, "su" );

    public final static String IDENTITY_NODE_NAME = "identity";

    public final static String ROLES_NODE_NAME = "roles";

    public final static String GROUPS_NODE_NAME = "groups";

    public final static String USERS_NODE_NAME = "users";

    private final UserStoreKey userStore;

    private final PrincipalType type;

    private final String principalId;

    private final String refString;

    private PrincipalKey( final UserStoreKey userStore, final PrincipalType type, final String principalId )
    {
        checkArgument( ( type == PrincipalType.ROLE ) || ( userStore != null ), "Principal user store cannot be null" );
        this.userStore = userStore;
        this.type = checkNotNull( type, "Principal type cannot be null" );
        checkArgument( !Strings.isNullOrEmpty( principalId ), "Principal id cannot be null or empty" );
        this.principalId = CharacterChecker.check( principalId, "Not a valid principal key [" + principalId + "]" );
        if ( type == PrincipalType.ROLE )
        {
            this.refString = Joiner.on( SEPARATOR ).join( type.toString().toLowerCase(), principalId );
        }
        else
        {
            this.refString = Joiner.on( SEPARATOR ).join( type.toString().toLowerCase(), userStore.toString(), principalId );
        }
    }

    private PrincipalKey()
    {
        this.userStore = UserStoreKey.system();
        this.type = PrincipalType.USER;
        this.principalId = "anonymous";
        this.refString = Joiner.on( SEPARATOR ).join( type.toString().toLowerCase(), userStore.toString(), principalId );
    }

    public UserStoreKey getUserStore()
    {
        return userStore;
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

    public NodePath toPath()
    {
        if ( this.isRole() )
        {
            return NodePath.create( NodePath.ROOT ).
                addElement( IDENTITY_NODE_NAME ).
                addElement( ROLES_NODE_NAME ).
                addElement( getId() ).
                build();
        }
        else
        {
            final String folderName = this.isGroup() ? GROUPS_NODE_NAME : USERS_NODE_NAME;
            return NodePath.create( NodePath.ROOT ).
                addElement( IDENTITY_NODE_NAME ).
                addElement( getUserStore().toString() ).
                addElement( folderName ).
                addElement( getId() ).
                build();
        }
    }

    @Override
    public String toString()
    {
        return refString;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof PrincipalKey ) && ( (PrincipalKey) o ).refString.equals( this.refString );
    }

    @Override
    public int hashCode()
    {
        return this.refString.hashCode();
    }

    public static PrincipalKey from( final String principalKey )
    {
        checkArgument( !Strings.isNullOrEmpty( principalKey ), "Principal key cannot be null or empty" );
        if ( ANONYMOUS_PRINCIPAL.toString().equals( principalKey ) )
        {
            return ANONYMOUS_PRINCIPAL;
        }

        final Matcher matcher = REF_PATTERN.matcher( principalKey );
        if ( !matcher.find() )
        {
            throw new IllegalArgumentException( "Not a valid principal key [" + principalKey + "]" );
        }

        final String typeStr;
        final UserStoreKey userStoreKey;
        final String id;
        if ( matcher.group( 1 ) != null )
        {
            typeStr = matcher.group( 1 );
            userStoreKey = null;
            id = matcher.group( 2 );
        }
        else
        {
            typeStr = matcher.group( 3 );
            final String userStore = matcher.group( 4 );
            userStoreKey = UserStoreKey.from( userStore );
            id = matcher.group( 5 );
        }
        final PrincipalType type = PrincipalType.valueOf( typeStr.toUpperCase() );

        return from( userStoreKey, type, id );
    }

    private static PrincipalKey from( final UserStoreKey userStoreKey, final PrincipalType type, final String id )
    {
        switch ( type )
        {
            case USER:
                return PrincipalKey.ofUser( userStoreKey, id );
            case GROUP:
                return PrincipalKey.ofGroup( userStoreKey, id );
            case ROLE:
                return PrincipalKey.ofRole( id );

            default:
                throw new IllegalArgumentException( "Not a valid principal type [" + type + "]" );
        }
    }

    public static PrincipalKey ofAnonymous()
    {
        return ANONYMOUS_PRINCIPAL;
    }

    public static PrincipalKey ofSuperUser()
    {
        return SUPER_USER_PRINCIPAL;
    }

    public static PrincipalKey ofUser( final UserStoreKey userStore, final String userId )
    {
        return new PrincipalKey( userStore, PrincipalType.USER, userId );
    }

    public static PrincipalKey ofGroup( final UserStoreKey userStore, final String groupId )
    {
        return new PrincipalKey( userStore, PrincipalType.GROUP, groupId );
    }

    public static PrincipalKey ofRole( final String roleId )
    {
        return new PrincipalKey( null, PrincipalType.ROLE, roleId );
    }
}
