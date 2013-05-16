package com.enonic.wem.core.account;

import javax.inject.Inject;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.account.ChangePassword;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;


public final class ChangePasswordHandler
    extends CommandHandler<ChangePassword>
{
    private AccountDao accountDao;

    public ChangePasswordHandler()
    {
        super( ChangePassword.class );
    }

    @Override
    public void handle( final CommandContext context, final ChangePassword command )
        throws Exception
    {
        final AccountKey user = command.getKey();
        final boolean userExists = accountDao.accountExists( user, context.getJcrSession() );
        if ( !userExists )
        {
            throw new AccountNotFoundException( user );
        }
        // TODO implement the actual password change (cannot use SecurityService.changePassword since it does not use JCR)

        // TODO return false if password could not be changed, due to password policy for example.
        command.setResult( true );
    }

    @Inject
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
