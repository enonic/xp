package com.enonic.wem.core.account;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.account.DeleteAccount;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandHandler;


public class DeleteAccountHandler
    extends CommandHandler<DeleteAccount>
{
    private AccountDao accountDao;

    @Override
    public void handle()
        throws Exception
    {
        final AccountKey accountKey = command.getKey();
        final Session session = context.getJcrSession();

        final boolean accountDeleted = this.accountDao.deleteAccount( accountKey, context.getJcrSession() );

        session.save();

        command.setResult( accountDeleted );
    }

    @Inject
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }

}
