package com.enonic.wem.core.account;

import javax.inject.Inject;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.account.ValidatePassword;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandHandler;


public final class ValidatePasswordHandler
    extends CommandHandler<ValidatePassword>
{
    private AccountDao accountDao;

    @Override
    public void handle( final ValidatePassword command )
        throws Exception
    {
        final AccountKey user = command.getKey();
        final boolean userExists = accountDao.accountExists( user, context.getJcrSession() );
        if ( !userExists )
        {
            throw new AccountNotFoundException( user );
        }

        // TODO implement the actual password validation (cannot use SecurityService.loginPortalUser since it does not use JCR)
        command.setResult( true );
    }

    @Inject
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
