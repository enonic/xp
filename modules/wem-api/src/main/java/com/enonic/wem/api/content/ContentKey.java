package com.enonic.wem.api.content;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.wem.api.account.AccountType;

public final class ContentKey
{
    private final static ContentKey ANON_USER = new ContentKey( AccountType.USER, "system", "anonymous" );

    private final static ContentKey SUPER_USER = new ContentKey( AccountType.USER, "system", "admin" );

    private final static char SEPARATOR = ':';

    private final static Pattern REF_PATTERN = Pattern.compile( "^(user|group|role):([^:]+):([^:]+)$" );

    private final AccountType type;

    private final String userStore;

    private final String localName;

    private final String refString;

    private final String qualifiedName;

    private ContentKey( final AccountType type, final String userStore, final String localName )
    {
        this.type = type;
        this.userStore = userStore;
        this.localName = localName;
        this.qualifiedName = Joiner.on( SEPARATOR ).join( this.userStore, this.localName );
        this.refString = Joiner.on( SEPARATOR ).join( this.type.toString().toLowerCase(), this.qualifiedName );
    }

    public AccountType getType()
    {
        return this.type;
    }

    public boolean isUser()
    {
        return this.type == AccountType.USER;
    }

    public boolean isGroup()
    {
        return this.type == AccountType.GROUP;
    }

    public boolean isRole()
    {
        return this.type == AccountType.ROLE;
    }

    public String getUserStore()
    {
        return this.userStore;
    }

    public String getLocalName()
    {
        return this.localName;
    }

    public String getQualifiedName()
    {
        return this.qualifiedName;
    }

    public boolean isSuperUser()
    {
        return SUPER_USER.equals( this );
    }

    public boolean isAnonymous()
    {
        return ANON_USER.equals( this );
    }

    public boolean isBuiltIn()
    {
        return isRole() || isAnonymous() || isSuperUser();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof ContentKey ) && ( (ContentKey) o ).refString.equals( this.refString );
    }

    public int hashCode()
    {
        return this.refString.hashCode();
    }

    public String toString()
    {
        return this.refString;
    }

    public static ContentKey anonymous()
    {
        return ANON_USER;
    }

    public static ContentKey superUser()
    {
        return SUPER_USER;
    }

    public static ContentKey from( final String value )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( value ), "Account key cannot be null or empty" );

        final Matcher matcher = REF_PATTERN.matcher( value );
        if ( !matcher.find() )
        {
            throw new IllegalArgumentException( "Not a valid account key [" + value + "]" );
        }

        final AccountType type = AccountType.valueOf( matcher.group( 1 ).toUpperCase() );
        final String userStore = matcher.group( 2 );
        final String localName = matcher.group( 3 );

        return new ContentKey( type, userStore, localName );
    }

    private static ContentKey from( final AccountType type, final String qName )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( qName ), "Account qualified name cannot be null or empty" );
        return from( type.toString().toLowerCase() + ":" + qName );
    }

    public static ContentKey user( final String qName )
    {
        return from( AccountType.USER, qName );
    }

    public static ContentKey group( final String qName )
    {
        return from( AccountType.GROUP, qName );
    }

    public static ContentKey role( final String qName )
    {
        return from( AccountType.ROLE, qName );
    }
}
