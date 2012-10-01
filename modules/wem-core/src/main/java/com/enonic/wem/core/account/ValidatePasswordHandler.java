package com.enonic.wem.core.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.account.ValidatePassword;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public final class ValidatePasswordHandler
    extends CommandHandler<ValidatePassword>
{
    private AccountDao accountDao;

    public ValidatePasswordHandler()
    {
        super( ValidatePassword.class );
    }

    @Override
    public void handle( final CommandContext context, final ValidatePassword command )
        throws Exception
    {
        final AccountKey user = command.getKey();
        final boolean userExists = accountDao.accountExists( context.getJcrSession(), user );
        if ( !userExists )
        {
            throw new AccountNotFoundException( user );
        }

        // TODO implement the actual password validation (cannot use SecurityService.loginPortalUser since it does not use JCR)
        command.setResult( true );
    }

    @Autowired
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
