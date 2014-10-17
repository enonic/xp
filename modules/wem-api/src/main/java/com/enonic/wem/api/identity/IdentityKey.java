package com.enonic.wem.api.identity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public final class IdentityKey
{
    private final static String SEPARATOR = ":";

    private final static Pattern REF_PATTERN = Pattern.compile( "^([^:]+):(user|group|agent):([^:]+)$" );

    private static final IdentityKey ANONYMOUS_IDENTITY = new IdentityKey();

    private final RealmKey realm;

    private final IdentityType type;

    private final String identityId;

    private final String refString;

    private IdentityKey( final RealmKey realm, final IdentityType type, final String identityId )
    {
        Preconditions.checkNotNull( realm, "Identity realm cannot be null" );
        Preconditions.checkNotNull( type, "Identity type cannot be null" );
        Preconditions.checkArgument( !Strings.isNullOrEmpty( identityId ), "Identity id cannot be null or empty" );
        this.realm = realm;
        this.type = type;
        this.identityId = identityId;
        this.refString = Joiner.on( SEPARATOR ).join( realm.toString(), type.toString().toLowerCase(), identityId );
    }

    private IdentityKey()
    {
        this.realm = null;
        this.type = IdentityType.ANONYMOUS;
        this.identityId = "anonymous";
        this.refString = Joiner.on( SEPARATOR ).join( type.toString().toLowerCase(), identityId );
    }

    public RealmKey getRealm()
    {
        return realm;
    }

    public IdentityType getType()
    {
        return type;
    }

    public String getId()
    {
        return identityId;
    }

    public boolean isUser()
    {
        return this.type == IdentityType.USER;
    }

    public boolean isGroup()
    {
        return this.type == IdentityType.GROUP;
    }

    public boolean isAgent()
    {
        return this.type == IdentityType.AGENT;
    }

    public boolean isAnonymous()
    {
        return this.type == IdentityType.ANONYMOUS;
    }

    @Override
    public String toString()
    {
        return refString;
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof IdentityKey ) && ( (IdentityKey) o ).refString.equals( this.refString );
    }

    public int hashCode()
    {
        return this.refString.hashCode();
    }

    public static IdentityKey from( final String identityKey )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( identityKey ), "Identity key cannot be null or empty" );
        if ( ANONYMOUS_IDENTITY.toString().equals( identityKey ) )
        {
            return ANONYMOUS_IDENTITY;
        }

        final Matcher matcher = REF_PATTERN.matcher( identityKey );
        if ( !matcher.find() )
        {
            throw new IllegalArgumentException( "Not a valid identity key [" + identityKey + "]" );
        }

        final String realmStr = matcher.group( 1 );
        final RealmKey realm = new RealmKey( realmStr );
        final IdentityType type = IdentityType.valueOf( matcher.group( 2 ).toUpperCase() );
        final String id = matcher.group( 3 );

        switch ( type )
        {
            case USER:
                return IdentityKey.ofUser( realm, id );
            case GROUP:
                return IdentityKey.ofGroup( realm, id );
            case AGENT:
                return IdentityKey.ofAgent( realm, id );

            default:
                throw new IllegalArgumentException( "Not a valid identity key [" + identityKey + "]" );
        }
    }

    public static IdentityKey ofAnonymous()
    {
        return ANONYMOUS_IDENTITY;
    }

    public static IdentityKey ofUser( final RealmKey realm, final String identityId )
    {
        return new IdentityKey( realm, IdentityType.USER, identityId );
    }

    public static IdentityKey ofGroup( final RealmKey realm, final String identityId )
    {
        return new IdentityKey( realm, IdentityType.GROUP, identityId );
    }

    public static IdentityKey ofAgent( final RealmKey realm, final String identityId )
    {
        return new IdentityKey( realm, IdentityType.AGENT, identityId );
    }
}
