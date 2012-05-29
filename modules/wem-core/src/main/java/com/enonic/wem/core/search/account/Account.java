package com.enonic.wem.core.search.account;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;


public abstract class Account
    implements Comparable<Account>,Serializable
{
    private AccountKey key;

    private final AccountType type;

    private String name;

    private String displayName;

    private DateTime lastModified;

    private String userStoreName;

    protected Account(AccountType type)
    {
        this.type = type;
    }

    public String getQualifiedName()
    {
        if ( StringUtils.isEmpty( userStoreName ) )
        {
            return name;
        }
        else
        {
            return name + "\\" + userStoreName;
        }
    }

    public AccountKey getKey()
    {
        return key;
    }

    public void setKey( AccountKey key )
    {
        this.key = key;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public DateTime getLastModified()
    {
        return lastModified;
    }

    public void setLastModified( DateTime lastModified )
    {
        this.lastModified = lastModified;
    }

    public String getUserStoreName()
    {
        return userStoreName;
    }

    public void setUserStoreName( String userStoreName )
    {
        this.userStoreName = userStoreName;
    }

    public AccountType getType()
    {
        return type;
    }
    @Override
    public int compareTo( Account anotherAccount )
    {
        return this.getKey().compareTo( anotherAccount.getKey() );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        Account account = (Account) o;
        return getKey().equals( account.getKey());
    }

    @Override
    public int hashCode()
    {
        final int initialNonZeroOddNumber = 273;
        final int multiplierNonZeroOddNumber = 637;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( getKey() ).toHashCode();
    }

}
