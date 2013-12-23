package com.enonic.wem.api.account;

import com.enonic.wem.api.exception.BaseException;

public final class AccountNotFoundException
    extends BaseException
{
    public AccountNotFoundException( final AccountKey key )
    {
        super( "Account [{0}] was not found", key );
    }
}

