package com.enonic.wem.api.account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;

public final class UserKey
{
    private final static UserKey ANON_USER = new UserKey( "system", "anonymous" );

    private final static UserKey SUPER_USER = new UserKey( "system", "admin" );

    private final static Pattern REF_PATTERN = Pattern.compile( "^(user:)?([^:]+):([^:]+)$" );

    private final static char SEPARATOR = ':';

    private final String localName;

    private final String refString;

    private final String qualifiedName;

    private UserKey( final String userStore, final String localName )
    {
        this.localName = localName;
        this.qualifiedName = Joiner.on( SEPARATOR ).join( userStore, this.localName );
        this.refString = Joiner.on( SEPARATOR ).join( "user", this.qualifiedName );
    }

    public String getLocalName()
    {
        return this.localName;
    }

    public String getQualifiedName()
    {
        return this.qualifiedName;
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof UserKey ) && ( (UserKey) o ).refString.equals( this.refString );
    }

    public int hashCode()
    {
        return this.refString.hashCode();
    }

    public String toString()
    {
        return this.refString;
    }

    public static UserKey from( final String value )
    {
        final Matcher matcher = REF_PATTERN.matcher( value );
        if ( !matcher.find() )
        {
            throw new IllegalArgumentException( "Not a valid account key [" + value + "]" );
        }

        final String userStore = matcher.group( 2 );
        final String localName = matcher.group( 3 );
        return new UserKey( userStore, localName );
    }

    public static UserKey anonymous()
    {
        return ANON_USER;
    }

    public static UserKey superUser()
    {
        return SUPER_USER;
    }
}
