package com.enonic.wem.core.userstore;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.userstore.DeleteUserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandHandler;


public class DeleteUserStoreHandler
    extends CommandHandler<DeleteUserStore>
{
    private AccountDao accountDao;

    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();
        final UserStoreName userStoreName = command.getName();
        final boolean userStoreDeleted = accountDao.deleteUserStore( userStoreName, session );
        if ( userStoreDeleted )
        {
            session.save();
        }
        command.setResult( userStoreDeleted );
    }

    @Inject
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
