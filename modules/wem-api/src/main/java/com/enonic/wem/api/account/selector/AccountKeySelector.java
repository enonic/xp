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
}
