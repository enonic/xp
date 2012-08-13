package com.enonic.wem.api.account.selector;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeySet;

public final class AccountSelectors
{
    public static AccountKeySelector keys( final AccountKey... keys )
    {
        return keys( AccountKeySet.from( keys ) );
    }

    public static AccountKeySelector keys( final Iterable<AccountKey> keys )
    {
        return keys( AccountKeySet.from( keys ) );
    }

    public static AccountKeySelector keys( final String... keys )
    {
        return keys( AccountKeySet.from( keys ) );
    }

    public static AccountKeySelector keys( final AccountKeySet keys )
    {
        return new AccountKeySelectorImpl( keys );
    }
}
