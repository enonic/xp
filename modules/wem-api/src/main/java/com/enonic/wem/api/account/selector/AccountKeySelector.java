package com.enonic.wem.api.account.selector;

import com.enonic.wem.api.account.AccountKeys;

public final class AccountKeySelector
    implements AccountSelector
{
    private final AccountKeys keys;

    public AccountKeySelector( final AccountKeys keys )
    {
        this.keys = keys;
    }

    public AccountKeys getKeys()
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
