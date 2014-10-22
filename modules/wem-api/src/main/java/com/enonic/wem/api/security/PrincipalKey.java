package com.enonic.wem.api.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class PrincipalKey
{
    private final static String SEPARATOR = ":";

    private final static Pattern REF_PATTERN = Pattern.compile( "^([^:]+):(user|group|role):([^:]+)$" );

    private static final PrincipalKey ANONYMOUS_PRINCIPAL = new PrincipalKey();

    private final UserStoreKey userStore;

    private final PrincipalType type;

    private final String principalId;

    private final String refString;

    private PrincipalKey( final UserStoreKey userStore, final PrincipalType type, final String principalId )
    {
        this.userStore = checkNotNull( userStore, "Principal user store cannot be null" );
        this.type = checkNotNull( type, "Principal type cannot be null" );
        checkArgument( !Strings.isNullOrEmpty( principalId ), "Principal id cannot be null or empty" );
        this.principalId = principalId;
        this.refString = Joiner.on( SEPARATOR ).join( userStore.toString(), type.toString().toLowerCase(), principalId );
    }

    private PrincipalKey()
    {
        this.userStore = UserStoreKey.system();
        this.type = PrincipalType.USER;
        this.principalId = "anonymous";
        this.refString = Joiner.on( SEPARATOR ).join( userStore.toString(), type.toString().toLowerCase(), principalId );
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

    @Override
    public String toString()
    {
        return refString;
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof PrincipalKey ) && ( (PrincipalKey) o ).refString.equals( this.refString );
    }

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

        final String userStore = matcher.group( 1 );
        final UserStoreKey userStoreKey = new UserStoreKey( userStore );
        final PrincipalType type = PrincipalType.valueOf( matcher.group( 2 ).toUpperCase() );
        final String id = matcher.group( 3 );

        switch ( type )
        {
            case USER:
                return PrincipalKey.ofUser( userStoreKey, id );
            case GROUP:
                return PrincipalKey.ofGroup( userStoreKey, id );
            case ROLE:
                return PrincipalKey.ofRole( id );

            default:
                throw new IllegalArgumentException( "Not a valid principal key [" + principalKey + "]" );
        }
    }

    public static PrincipalKey ofAnonymous()
    {
        return ANONYMOUS_PRINCIPAL;
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
        return new PrincipalKey( UserStoreKey.system(), PrincipalType.ROLE, roleId );
    }
}
