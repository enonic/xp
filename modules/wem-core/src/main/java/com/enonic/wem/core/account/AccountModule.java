package com.enonic.wem.core.account;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.account.dao.AccountDaoImpl;
import com.enonic.wem.core.command.CommandBinder;

public final class AccountModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( AccountDao.class ).to( AccountDaoImpl.class ).in( Scopes.SINGLETON );

        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( ChangePasswordHandler.class );
        commands.add( CreateAccountHandler.class );
        commands.add( DeleteAccountHandler.class );
        commands.add( FindAccountsHandler.class );
        commands.add( FindMembersHandler.class );
        commands.add( FindMembershipsHandler.class );
        commands.add( GetAccountsHandler.class );
        commands.add( UpdateAccountsHandler.class );
        commands.add( ValidatePasswordHandler.class );
    }
}
