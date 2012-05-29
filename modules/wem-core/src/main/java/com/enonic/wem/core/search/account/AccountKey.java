package com.enonic.wem.core.search.account;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class AccountKey
    implements Comparable<AccountKey>, Serializable
{

    private String value;

    public AccountKey( String key )
    {
        this.value = key;
    }

    @Override
    public int compareTo(AccountKey arg0) {
      return this.value.compareTo(arg0.value);
    }

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

        AccountKey userKey = (AccountKey) o;

        if ( !value.equals( userKey.value ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        final int initialNonZeroOddNumber = 463;
        final int multiplierNonZeroOddNumber = 723;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( value ).toHashCode();
    }

    public String toString()
    {
        return value;
    }

}