package com.enonic.wem.api.account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

public final class AccountKey
{
    private final static AccountKey ANON_USER = new AccountKey( AccountType.USER, "system", "anonymous" );

    private final static AccountKey SUPER_USER = new AccountKey( AccountType.USER, "system", "admin" );

    private final static char SEPARATOR = ':';

    private final static Pattern REF_PATTERN = Pattern.compile( "^(user|group|role):([^:]+):([^:]+)$" );

    private final AccountType type;

    private final String userStore;

    private final String localName;

    private final String refString;

    private final String qualifiedName;

    private AccountKey( final AccountType type, final String userStore, final String localName )
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

    public boolean equals( final Object o )
    {
        return ( o instanceof AccountKey ) && ( (AccountKey) o ).refString.equals( this.refString );
    }

    public int hashCode()
    {
        return this.refString.hashCode();
    }

    public String toString()
    {
        return this.refString;
    }

    public static AccountKey anonymous()
    {
        return ANON_USER;
    }

    public static AccountKey superUser()
    {
        return SUPER_USER;
    }

    public static AccountKey from( final String value )
    {
        if ( Strings.isNullOrEmpty( value ) )
        {
            throw new IllegalArgumentException( "Account key cannot be null or empty" );
        }

        final Matcher matcher = REF_PATTERN.matcher( value );
        if ( !matcher.find() )
        {
            throw new IllegalArgumentException( "Not a valid account key [" + value + "]" );
        }

        final AccountType type = AccountType.valueOf( matcher.group( 1 ).toUpperCase() );
        final String userStore = matcher.group( 2 );
        final String localName = matcher.group( 3 );

        return new AccountKey( type, userStore, localName );
    }
}