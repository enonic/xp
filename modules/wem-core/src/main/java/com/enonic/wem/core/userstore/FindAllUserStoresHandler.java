package com.enonic.wem.core.userstore;

import javax.inject.Inject;

import com.enonic.wem.api.command.userstore.FindAllUserStores;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandHandler;


public class FindAllUserStoresHandler
    extends CommandHandler<FindAllUserStores>
{
    private AccountDao accountDao;

    @Override
    public void handle( final FindAllUserStores command )
        throws Exception
    {
        final UserStoreNames userStoreNames = accountDao.getUserStoreNames( context.getJcrSession() );
        command.setResult( UserStoreNames.from( userStoreNames ) );
    }

    @Inject
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
