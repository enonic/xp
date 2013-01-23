package com.enonic.wem.api.account;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.util.AbstractImmutableEntityList;

public final class Accounts
    extends AbstractImmutableEntityList<Account>
{
    private Accounts( final ImmutableList<Account> list )
    {
        super( list );
    }

    public AccountKeys getKeys()
    {
        final Collection<AccountKey> keys = Collections2.transform( this.list, new ToKeyFunction() );
        return AccountKeys.from( keys );
    }

    public static Accounts empty()
    {
        final ImmutableList<Account> list = ImmutableList.of();
        return new Accounts( list );
    }

    public static Accounts from( final Account... accounts )
    {
        return new Accounts( ImmutableList.copyOf( accounts ) );
    }

    public static Accounts from( final Iterable<? extends Account> accounts )
    {
        return new Accounts( ImmutableList.copyOf( accounts ) );
    }

    public static Accounts from( final Collection<? extends Account> accounts )
    {
        return new Accounts( ImmutableList.copyOf( accounts ) );
    }

    private final static class ToKeyFunction
        implements Function<Account, AccountKey>
    {
        @Override
        public AccountKey apply( final Account value )
        {
            return value.getKey();
        }
    }
}
