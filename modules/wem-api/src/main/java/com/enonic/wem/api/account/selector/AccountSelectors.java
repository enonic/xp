package com.enonic.wem.api.account.selector;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;

public abstract class AccountSelectors
{
    public static AccountKeySelector keys( final AccountKey... keys )
    {
        return keys( AccountKeys.from( keys ) );
    }

    public static AccountKeySelector keys( final Iterable<AccountKey> keys )
    {
        return keys( AccountKeys.from( keys ) );
    }

    public static AccountKeySelector keys( final String... keys )
    {
        return keys( AccountKeys.from( keys ) );
    }

    public static AccountKeySelector keys( final AccountKeys keys )
    {
        return new AccountKeySelector( keys );
    }

    public static AccountQuery query( final String query )
    {
        return new AccountQuery( query );
    }

    public static AccountQuery queryByEmail( final String email )
    {
        return new AccountQuery( "" ).email( email );
    }
}
