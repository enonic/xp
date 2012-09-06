package com.enonic.wem.api.account.selector;

import com.enonic.wem.api.account.AccountKeySet;

public final class AccountKeySelector
    implements AccountSelector
{
    private final AccountKeySet keys;

    public AccountKeySelector( final AccountKeySet keys )
    {
        this.keys = keys;
    }

    public AccountKeySet getKeys()
    {
        return this.keys;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final AccountKeySelector that = (AccountKeySelector) o;

        if ( keys != null ? !keys.equals( that.keys ) : that.keys != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return keys != null ? keys.hashCode() : 0;
    }
}
