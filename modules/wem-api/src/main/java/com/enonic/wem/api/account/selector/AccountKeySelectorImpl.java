package com.enonic.wem.api.account.selector;

import com.enonic.wem.api.account.AccountKeySet;

final class AccountKeySelectorImpl
    implements AccountKeySelector
{
    private final AccountKeySet keys;

    public AccountKeySelectorImpl( final AccountKeySet keys )
    {
        this.keys = keys;
    }

    @Override
    public AccountKeySet getKeys()
    {
        return this.keys;
    }
}
